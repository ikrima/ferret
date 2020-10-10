(defmacro wdt-enable [timer]
  (let [timer (condp = timer
                15   "WDTO_15MS"
                30   "WDTO_30MS"
                60   "WDTO_60MS"
                120  "WDTO_120MS"
                250  "WDTO_250MS"
                500  "WDTO_500MS"
                1000 "WDTO_1S"
                2000 "WDTO_2S"
                4000 "WDTO_4S"
                8000 "WDTO_8S")]
    `(cxx ~(str "wdt_enable(" timer ")"))))

(defnative wdt-reset []
  (on "defined __AVR_ARCH__"
      ("avr/wdt.h")
      "wdt_reset();"))
