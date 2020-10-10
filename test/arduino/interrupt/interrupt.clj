(require '[ferret.arduino :as gpio])

(def input-pin  3)
(def debug-pin 13)

(gpio/pin-mode debug-pin :output)

(defn control-light []
  (->> (gpio/digital-read  input-pin)
       (gpio/digital-write debug-pin)))

(gpio/attach-interrupt input-pin :change control-light)

(forever
 (sleep 100))
