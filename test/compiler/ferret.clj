(ns ferret
  (:refer-clojure :exclude [compile])
  (:use [compiler.core] :reload)
  (:require [compiler.parser :as parser])
  (:use [clojure.test]
        [clojure.java.shell]
        [clojure.tools [logging :only [warn info]]]))

(defn check-form [form & [opts]]
  (let [options (merge (compile-options) opts)]
    (compile->cpp form options)
    (compile->binary options)
    (with-sh-dir "./"
      (let [exit (->> (sh "./solution") :exit)]
        (sh "rm" "-rf" "solution")
        (sh "rm" "-rf" "solution.cpp")
        exit))))

(deftest test-fn->lift
  (let [prg-a (compile '((defn one-plus-one []
                           (+ 1 1))

                         (while true
                           (+ 1 1))) {})
        prg-b (fn->lift
               '(fn* outer [a]
                     (fn* inner-a [b]
                          (+ a b))
                     (fn* inner-b [c] c)))

        prg-c (fn->lift
               '((fn* inner-a [a]
                      ((fn* inner-b [b]
                            ((fn* inner-c [c] (+ b c))
                             3))
                       2))
                 1))
        prg-d (fn->lift
               '((fn* inner-a [a]
                      ((fn* inner-b [b]
                            ((fn* inner-c [c] (+ b))
                             3))
                       2))
                 1))]
    ;;while shoud use one-plus-one in its body
    ;;check fn-defined?
    (is (= 2 (count (parser/peek prg-a (fn [f] (= 'one_plus_one f))))))

    (is (= 1 (->> (fn [f] (= '(fir-defn-heap inner-a (a) [b] (+ a b)) f))
                  (parser/peek prg-b)
                  count)))
    (is (= 1 (->> (fn [f] (= '(fir-defn-heap inner-b () [c] c) f))
                  (parser/peek prg-b)
                  count)))
    (is (= 1 (->> (fn [f] (= '(fir-defn-heap inner-c (b) [c] (+ b c)) f))
                  (parser/peek prg-c)
                  count)))
    (is (= 1 (->> (fn [f] (= '(fir-defn-heap inner-c (b) [_] (+ b)) f))
                  (parser/peek prg-d)
                  count)))))
(deftest test-fn->inline
  (let [prg-a (compile '((defn fn-inline [x] x)
                         (defn ^volatile fn-no-inline [y] y)
                         (fn-inline 42)
                         (fn-no-inline 42)) {})]
    (is (= 1 (->> (fn [f] (= '(fn_no_inline 42) f))
                  (parser/peek prg-a)
                  count)))
    (is (= 1 (->> (fn [f] (= '((fir_fn_stack fn_inline) 42) f))
                  (parser/peek prg-a)
                  count)))))
(deftest three-shaking
  (is (= '((defn c [] 1)
           (defn b [] (c))
           (defn a [] (b))
           (a))
         (shake-concat '((defn no-call-a [])
                         (defnative no-call-b [] (on "" ""))
                         (defn c [] 1)
                         (defn b [] (c))
                         (defn a [] (b)))
                       '((a)))))

  (is (= '((defn y [])
           (let [a 1]
             (defn b []))
           (println (b) (y)))
         (shake-concat '((defn x [] )
                         (defn y [] )
                         (let [a 1]
                           (defn b [] )
                           (defn c [] a)))
                       '((println (b) (y))))))

  (is (= '((defn p-create []) (defn p-update []))
         (take 2 (shake-concat '((defn p-create [])
                                 (defn p-update [])
                                 (defmacro pc [& options]
                                   `(let [controller# (p-create)]
                                     (fn [input#] (p-update)))))
                               '((pc))))))

  (is (= '(defn new-lazy-seq [f] )
         (first (shake-concat '((defn new-lazy-seq [f] )
                                (defmacro lazy-seq [& body]
                                  `(new-lazy-seq (fn [] ~@body)))
                                (defn range
                                  ([high]
                                   (range 0 high))
                                  ([low high]
                                   (if (< low high)
                                     (cons low (lazy-seq
                                                (range (inc low) high)))))))
                              '((range 10)))))))
(deftest test-escape-analysis
  (let [prg-a (compile '((defn self [x] x)
                         (self 42)) {})
        prg-b (compile '((defn self [x] x)
                         (self self)) {})

        prg-c (compile '((defn multi ([x] x))) {})]

    (is (not (empty? (parser/peek prg-a (parser/form? 'fir_defn_stack)))))
    (is (not (empty? (parser/peek
                      prg-a (fn [f] (= '(fir_fn_stack self) f))))))

    (is (not (empty? (parser/peek prg-b (parser/form? 'fir_defn_heap)))))
    (is (not (empty? (parser/peek
                      prg-b (fn [f] (= '((fir_fn_stack self) (fir_fn_heap self)) f))))))

    (is (= (->> (parser/peek prg-c (parser/form? 'fir_defn_arity))
                first second first second second)
           (->> (parser/peek prg-c (parser/form? 'fir_defn_stack)) first second)))))
(defn aborted? [return-code]
  (= 134 return-code))

(deftest test-unit-test
  (is (aborted? (check-form '((assert (= 2 1))))))
  (is (zero?    (check-form '((assert (= 2 1))) {:release true}))))
(defn aborted? [return-code]
  (= 134 return-code))

(deftest test-unit-test
  (is (zero?    (check-form '((run-all-tests)))))
  (is (aborted? (check-form '((deftest some-test
                                (is (= 2 3)))
                              (run-all-tests)))))
  (is (zero?    (check-form '((deftest some-test
                                (is (= 2 2)))
                              (run-all-tests)))))
  (is (aborted? (check-form '((deftest some-test
                                (is (= 5 (apply + (list 1 2 3)))))
                              (run-all-tests)))))

  (is (zero?    (check-form '((deftest some-test
                                (is (= 6 (apply + (list 1 2 3)))))
                              (run-all-tests))))))
