(configure-runtime! FERRET_MEMORY_POOL_SIZE 512
                    FERRET_MEMORY_POOL_PAGE_TYPE byte)

(require '[ferret.arduino :as gpio])

(def yellow-led 13)
(def blue-led   12)

(gpio/pin-mode yellow-led :output)
(gpio/pin-mode blue-led   :output)

(defn make-led-toggler [pin]
  (fn []
    (->> (gpio/digital-read pin)
         (bit-xor 1)
         (gpio/digital-write pin))))

(def job-one
  (fn-throttler (make-led-toggler yellow-led) 5 :second :non-blocking))

(def job-two
  (fn-throttler (make-led-toggler blue-led)  20 :second :non-blocking))

(forever
 (job-one)
 (job-two))
