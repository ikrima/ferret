(require '[ferret.arduino :as gpio])

(def button (gpio/new-bounce 7 250))

(while true
  (when (pos? (button))
    (println "Pressed!"))
  (sleep 250))
