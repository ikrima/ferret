(defmacro pin-mode [pin mode]
  (let [mode    (-> mode name .toUpperCase)
        isr-pin (gensym)]
    `(do
       (def ~isr-pin ~pin)
       (cxx
        ~(str "::pinMode(number::to<int>(" isr-pin ") , " mode ");")))))

(defnative digital-write [^number_t pin ^number_t val]
  (on "defined FERRET_HARDWARE_ARDUINO"
      "::digitalWrite(pin, val);"))

(defnative digital-read [^number_t pin]
  (on "defined FERRET_HARDWARE_ARDUINO"
      "return obj<number>(::digitalRead(pin));"))

(defnative analog-write [^number_t pin ^number_t val]
  (on "defined FERRET_HARDWARE_ARDUINO"
      "::analogWrite(pin,val);"))

(defnative analog-read [^number_t pin]
  (on "defined FERRET_HARDWARE_ARDUINO"
      "return obj<number>(::analogRead(pin));"))

(defnative analog-write-resolution [^number_t bit]
  (on "defined FERRET_HARDWARE_ARDUINO"
      "::analogWriteResolution(bit);"))

(defnative analog-read-resolution [^number_t bit]
  (on "defined FERRET_HARDWARE_ARDUINO"
      "::analogReadResolution(bit);"))

(defobject bounce "arduino/bounce_o.h")

(defn new-bounce [^number_t x ^number_t t-debounce]
  "return obj<bounce>(x, t_debounce);")

(defnative random-seed [^number_t pin]
  (on "defined FERRET_HARDWARE_ARDUINO"
      "randomSeed(analogRead(pin));"))

(defnative random [^number_t x]
  (on "defined FERRET_HARDWARE_ARDUINO"
      "return obj<number>(random(x));"))

(defnative tone [^number_t pin ^number_t freq]
  (on "defined FERRET_HARDWARE_ARDUINO"
      "::tone(pin, freq);"))

(defnative no-tone [^number_t pin]
  (on "defined FERRET_HARDWARE_ARDUINO"
      "::noTone(pin);"))

(defmacro attach-interrupt [pin mode callback]
  (let [mode    (-> mode name .toUpperCase)
        isr-fn  (gensym)
        isr-pin (gensym)]
    `(do
       (def ~isr-fn  ~callback)
       (def ~isr-pin ~pin)
       (cxx
        ~(str "::pinMode(number::to<int>(" isr-pin ") , INPUT_PULLUP);\n"
              "auto isr_pin = digitalPinToInterrupt(number::to<int>(" isr-pin "));\n"
              "::attachInterrupt(isr_pin, [](){ run(" isr-fn ");}, " mode ");")))))

(defmacro no-interrupt [& body]
  `(no-interrupt-aux  (fn [] ~@body)))

(defn no-interrupt-aux [f]
  "noInterrupts();
   __result = run(f);
   interrupts();")

(defn detach-interrupt [^number_t p]
  "detachInterrupt(digitalPinToInterrupt(p));")

(defnative spi-begin []
  (on "defined FERRET_HARDWARE_ARDUINO"
      ("SPI.h")
      "SPI.begin();"))

(defn spi-end []
  "SPI.end();")

(defmacro spi-settings [max-speed data-order data-mode]
  (let [speed      (* max-speed 1000000)
        data-order (if (= data-order :msb-first)
                     "MSBFIRST"
                     "LSBFIRST")
        data-mode  (condp = data-mode
                     :mode-0 "SPI_MODE0"
                     :mode-1 "SPI_MODE1"
                     :mode-2 "SPI_MODE2"
                     :mode-3 "SPI_MODE3")]
    `(cxx ~(str "return obj<value<SPISettings>>(" speed "," data-order "," data-mode ");"))))

(defn with-spi-aux [conf f]
  "SPI.beginTransaction(value<SPISettings>::to_reference(conf));
   __result = run(f);
   SPI.endTransaction();")

(defmacro with-spi [conf & body]
  `(with-spi-aux ~conf (fn [] ~@body)))

(defn spi-write [^number_t val]
  "return obj<number>(SPI.transfer(val));")

(defnative wire-begin []
  (on "defined FERRET_HARDWARE_ARDUINO"
      ("Wire.h")
      "Wire.begin();"))

(defn with-wire-aux [^number_t addr f]
  "Wire.beginTransmission(addr);
   __result = run(f);
   Wire.endTransmission();")

(defmacro with-wire [addr & body]
  `(with-wire-aux ~addr (fn [] ~@body)))

(defn wire-write [^number_t val]
  "Wire.write(val);")

(defn wire-read []
  "return obj<number>(Wire.read());")

(defn wire-request-from [^number_t addr ^number_t bytes]
  "Wire.requestFrom(addr, bytes);")

(defn wire-available []
  "__result = obj<number>(Wire.available());")
