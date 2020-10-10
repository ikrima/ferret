class bounce final : public lambda_i {
  mutex lock;
  void (bounce::* fsm_state)();
  byte state;
  byte last_state;
  unsigned long t_debounce;
  unsigned long t_last_debounce;
  byte pin;

  void debounce(){
    int reading = digitalRead(pin);

    // reset the debouncing timer
    if (reading != last_state){
      t_last_debounce = millis();
      last_state = reading;
    }

    if ((::millis() - t_last_debounce) > t_debounce){
      if (reading == LOW)
        fsm_state = &bounce::off;
      else
        fsm_state = &bounce::on;
    }
  }

  void init(){
    pinMode(pin, INPUT);
    fsm_state = &bounce::debounce;
  }

  void on(){
    state = 1;
    fsm_state = &bounce::debounce;
  }

  void off(){
    state = 0;
    fsm_state = &bounce::debounce;
  }

  var step(){
    lock_guard guard(lock);
    (this->*fsm_state)();
    return obj<number>(state);
  }

 public:

  explicit bounce(number_t p, number_t t_db) :
    fsm_state(&bounce::init),
    state(0),
    last_state(0),
    t_debounce(t_db),
    t_last_debounce(millis()),
    pin(p)
    {}

  var invoke(ref args) const final {
    return var((object*)this).cast<bounce>()->step();
  }
};
