;;; blink.clj
(require '[ferret.arduino :as gpio])

(gpio/pin-mode 13 :output)

(forever
 (gpio/digital-write 13 1)
 (sleep 500)
 (gpio/digital-write 13 0)
 (sleep 500))
