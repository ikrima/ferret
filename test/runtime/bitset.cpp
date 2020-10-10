#include <cassert>
#include <runtime.h>

int main() {
  using namespace ferret;

  assert(FERRET_BITSET_USE_COMPILER_INTRINSICS == true);
  
  bitset<32> bs_a;
  assert(32 == bs_a.ffs(0));
  for(size_t i = 0; i < 2; i++)  bs_a.set(i);
  assert(0  == bs_a.ffs(0));
  assert(1  == bs_a.ffs(1));
  for(size_t i = 7; i < 16; i++) bs_a.set(i);
  assert(2  == bs_a.ffr(0));
  assert(5  == bs_a.ffr(5));
  assert(16 == bs_a.ffr(10));
  assert(32 == bs_a.ffs(31));

  bitset<64> bs_b;
  assert(0  == bs_b.ffr(0));
  assert(64 == bs_b.ffs(0));
  for(size_t i = 0; i < 8; i++)   bs_b.set(i);
  assert(0  == bs_b.ffs(0));
  assert(5  == bs_b.ffs(5));
  assert(8  == bs_b.ffr(5));
  for(size_t i = 16; i < 48; i++) bs_b.set(i);
  assert(16 == bs_b.ffs(8));
  assert(48 == bs_b.ffr(16));

  bitset<1024> bs_c;
  assert(0     == bs_c.ffr(0));
  assert(1024  == bs_c.ffs(0));
  for(size_t i = 0; i < 32; i++)  bs_c.set(i);
  assert(0     == bs_c.ffs(0));
  assert(32    == bs_c.ffr(0));
  for(size_t i = 256; i < 512; i++) bs_c.set(i);
  assert(256   == bs_c.ffs(256));
  assert(512   == bs_c.ffr(256));
  for(size_t i = 768; i < 1024; i++) bs_c.set(i);
  assert(1024  == bs_c.ffr(768));

  bitset<1024> bs_d;
  assert(0    == bs_d.ffr(0));
  bs_d.flip(0);
  assert(1    == bs_d.ffr(0));
  bs_d.flip(0);
  assert(0    == bs_d.ffr(0));

  assert(1024 == bs_d.ffs(0));
  bs_d.set(0, 1024);
  assert(0 == bs_d.ffs(0));
  bs_d.reset(0, 1024);
  assert(1024 == bs_d.ffs(0));
  bs_d.flip(0, 1024);
  assert(0 == bs_d.ffs(0));
  bs_d.flip(0, 1024);
  assert(1024 == bs_d.ffs(0));

  bs_d.set(256,512);
  bs_d.set(768,1024);

  assert(512  == bs_d.ffr(256));
  assert(256  == bs_d.ffs(256));
  assert(1024 == bs_d.ffr(768));
  assert(768  == bs_d.ffs(768));

  bs_d.reset(0,1024);
  for(size_t i = 0; i < 87; i++)  bs_d.set(i);
  for(size_t i = 90; i < 94; i++)  bs_d.set(i);
  for(size_t i = 106; i < 111; i++)  bs_d.set(i);
  for(size_t i = 136; i < 149; i++)  bs_d.set(i);

  assert(106 == bs_d.ffs(94,100));

  assert((15    == bitset<8,unsigned char>::bit_block(0,4)));
  assert((15    == bitset<32,unsigned int>::bit_block(0,4)));
  assert((60    == bitset<32,unsigned int>::bit_block(2,4)));
  assert((1024  == bitset<32,unsigned int>::bit_block(10,1)));
  assert((3072  == bitset<32,unsigned int>::bit_block(10,2)));
  assert((98304 == bitset<32,unsigned int>::bit_block(15,2)));
  assert((-1U   == bitset<32,unsigned int>::bit_block(0,60)));
  assert((-1U   == bitset<32,unsigned int>::bit_block(0,32)));

  return 0;
}
