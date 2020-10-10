#define FERRET_MEMORY_POOL_SIZE 4_MB
#define FERRET_BITSET_WORD_TYPE unsigned int

#include <cassert>
#include <runtime.h>

int main() {
  using namespace ferret::memory;
  using namespace allocator;

  assert(0  == align_req(0,8));
  assert(7  == align_req(1,8));
  assert(0  == align_req(8,8));

  assert(0  == align_of(0,8));
  assert(8  == align_of(1,8));
  assert(8  == align_of(8,8));

  alignas(16) int buff [4];
  assert(0  == align_req<int16_t>(buff));
  assert(reinterpret_cast<std::uintptr_t>(buff) == align_of<int16_t>(buff));

  size_t byte_s = sizeof(ferret::byte);

  memory_pool<ferret::byte, 8, unsigned char> nano_pool;

  void* a = nano_pool.allocate(byte_s);
  assert(nullptr  != a);
  assert(2        == nano_pool.used.ffr(0));
  assert(nullptr  != nano_pool.allocate(byte_s));
  assert(4        == nano_pool.used.ffr(0));

  void* c = nano_pool.allocate(byte_s);

  assert(nullptr  != c);
  assert(6        == nano_pool.used.ffr(0));
  assert(nullptr  != nano_pool.allocate(byte_s));

  nano_pool.free(c);

  assert(4        == nano_pool.used.ffr(0));
  assert(6        == nano_pool.used.ffs(4));
  assert(nullptr  != nano_pool.allocate(byte_s));

  memory_pool<ferret::byte, 16, unsigned char> tiny_pool;

  assert(0        == tiny_pool.used.ffr(0));
  assert(nullptr  != tiny_pool.allocate(byte_s * 2));
  assert(3        == tiny_pool.used.ffr(0));

  void* p = tiny_pool.allocate(byte_s * 4);

  assert(nullptr  != p);
  assert(8        == tiny_pool.used.ffr(0));

  tiny_pool.free(p);

  assert(3        == tiny_pool.used.ffr(0));
  assert(nullptr  == tiny_pool.allocate(byte_s * 40));
  assert(nullptr  != tiny_pool.allocate(byte_s * 6));
  assert(nullptr  != tiny_pool.allocate(byte_s * 1));
  assert(nullptr  != tiny_pool.allocate(byte_s * 1));
  assert(nullptr  == tiny_pool.allocate(byte_s * 10));

  memory_pool<uint64_t, 256> big_pool;

  assert(0        == big_pool.used.ffr(0));

  p = big_pool.allocate(1);

  assert(nullptr  != p);

  assert(2        == big_pool.used.ffr(0));

  big_pool.free(p);

  assert(0        == big_pool.used.ffr(0));
  assert(nullptr  == big_pool.allocate(2048));
  assert(0        == big_pool.used.ffr(0));

  return 0;
}
