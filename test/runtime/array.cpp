#include <cassert>
#include <runtime.h>

int main() {
  using namespace ferret;

  array<char> buffer(16);
  assert((buffer.size() == 16));

  array<char> str("abcde",5);
  assert((str[0] == 'a'));
  assert((str[1] == 'b'));
  assert((str[4] == 'e'));

  array<int> numbers {1, 2, 3, 4, 5};
  assert((numbers[0] == 1));
  assert((numbers[1] == 2));
  assert((numbers[2] == 3));
  assert((numbers[3] == 4));
  assert((numbers[4] == 5));

  // cppcheck-suppress useStlAlgorithm
  for (int& x : numbers) { x++; }

  assert((numbers[0] == 2));
  assert((numbers[1] == 3));
  assert((numbers[2] == 4));
  assert((numbers[3] == 5));
  assert((numbers[4] == 6));

  return 0;
}
