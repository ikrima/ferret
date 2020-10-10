#include <cassert>
#include <runtime.h>

int main() {
  typedef ferret::fixed_real<32,8> fix_32;
  typedef ferret::fixed_real<64,8> fix_64;

  // Test Casting
  assert((char)          fix_32(char(25))  == char(25));
  assert((int)           fix_32(int(1024)) == int(1024));
  assert((long)          fix_64(long(25))  == long(25));
  assert((unsigned long) fix_64(2500UL)    == 2500UL);

  long max_int = std::numeric_limits<int>::max() + 1024L;
  assert((long)fix_64(max_int) == ((long)std::numeric_limits<int>::max() + 1024L));

  // Test Arithmetic
  fix_32 x;
  fix_32 y;
  x = 10;
  y = 0.250;
  assert(10.25 == (double)(x + y));

  x = fix_32(0);
  for(int i = 0; i < 100; i++)
    x += fix_32(0.0625);
  assert((double)x == 6.25);

  x = fix_32(22.75);
  y = fix_32(12.5);
  assert((double)(x + y) == 35.25);

  x = fix_32(22.75);
  y = fix_32(22.5);
  assert((double)(x - y) ==  0.25);
  assert((double)(y - x) == -0.25);

  x = fix_32(-0.25);
  y = fix_32(4);
  assert((double)(x / y) ==  -0.0625);

  x = fix_32(-0.0625);
  y = fix_32(-10);
  assert((double)(x - y) ==  9.9375);

  x = fix_32(9.9375);
  y = fix_32(-3);
  assert((double)(x * y) ==  -29.8125);

  x = fix_32(-29.8125);
  y = fix_32(0.1875);
  assert((double)(x - y) ==  -30);

  return 0;
}
