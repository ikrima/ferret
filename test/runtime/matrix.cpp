#include <cassert>
#include <runtime.h>

int main() {
  using namespace ferret;
    
  //initializers
  assert((matrix::zeros(2,2)  == matrix::into<2,2>(0,0,0,0)));
  assert((matrix::ones(2,2)   == matrix::into<2,2>(1,1,1,1)));
  assert((matrix::eye(2)      == matrix::into<2,2>(1,0,0,1)));
  assert((matrix::full(2,2,4) == matrix::into<2,2>(4,4,4,4)));

  //shape
  assert((matrix::row_count(matrix::zeros(2,3))    == 2));
  assert((matrix::column_count(matrix::zeros(2,3)) == 3));

  //operations
  matrix ones  = matrix::ones(2,2);
  matrix zeros = matrix::zeros(2,2);
  matrix twos  = matrix::full(2,2,2);

  assert((ones - ones == zeros));
  assert((ones + ones == twos));
  assert((ones * 2    == twos));

  auto v3d = matrix::into<1,3>(0, 10, 0);

  assert((matrix::norm_euclidean(v3d) == 10));
  assert((matrix::normalise(v3d)      == matrix::into<1,3>(0, 1, 0)));
  return 0;
}
