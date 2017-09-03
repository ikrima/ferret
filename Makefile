DIR = $(shell pwd)
MAJOR_VERSION = $(shell git describe --abbrev=0 --tags)
MINOR_VERSION = $(shell git rev-list ${MAJOR_VERSION}.. --count)

DOCKER_RUN = docker run --rm -i -t -v "${DIR}":/ferret/ -w /ferret/ nakkaya/ferret-build
LEIN = cd src/ && lein

CPPWARNINGS = -pedantic -Werror -Wall -Wextra                    \
              -Wconversion -Wpointer-arith -Wmissing-braces      \
              -Woverloaded-virtual -Wuninitialized -Winit-self
CPPFLAGS = -std=c++11 ${CPPWARNINGS} -pthread

test: CPPSANITIZER = -fsanitize=undefined,address -fno-omit-frame-pointer

.PHONY: test-compiler test test-release packr deb deb-repo docs release docker-release clean
.PRECIOUS: %.cpp %.gcc %.clang

src/src/ferret/core.clj: ferret.org
	emacs -nw -Q --batch --eval "(progn (require 'org) (setq org-babel-use-quick-and-dirty-noweb-expansion t) (require 'ob) (find-file \"ferret.org\") (org-babel-tangle))"

bin/ferret : src/src/ferret/core.clj
	mkdir -p bin/
	${LEIN} uberjar
	cat src/resources/jar-sh-header src/target/ferret.jar > bin/ferret
	chmod +x bin/ferret
	mv src/target/ferret.jar bin/ferret.jar

%.cpp: %.clj
	bin/ferret -i $<
	cppcheck --quiet --std=c++11 --template=gcc --enable=all --error-exitcode=1 $@

%.gcc: %.cpp
	/usr/bin/g++ $(CPPFLAGS) $(CPPSANITIZER) -x c++ $< -o $@
	$@ 1 2

%.clang: %.cpp
	/usr/bin/clang++ $(CPPFLAGS) $(CPPSANITIZER) -x c++ $< -o $@
	$@ 1 2

%.cxx: %.cpp
	$(CXX) $(CPPFLAGS) $(CPPSANITIZER) -x c++ $< -o $@
	$@ 1 2

STD_LIB_TESTS = src/test/simple_module_main.clj         \
                src/test/import_module_main.clj         \
                src/test/import_module_empty_aux_a.clj  \
                src/test/import_module_empty_aux_b.clj  \
                src/test/memory_pool.clj                \
                src/test/runtime_all.clj

CLANG_OBJS=$(STD_LIB_TESTS:.clj=.clang)
GCC_OBJS=$(STD_LIB_TESTS:.clj=.gcc)
CXX_OBJS=$(STD_LIB_TESTS:.clj=.cxx)

test-compiler: src/src/ferret/core.clj
	${LEIN} test

test:     test-compiler bin/ferret $(CXX_OBJS)
test-release:  test-compiler bin/ferret $(GCC_OBJS) $(CLANG_OBJS)

packr:  
	cd src/ && bash resources/build-bundles
	mv src/*.zip bin/
deb:  
	mkdir -p deb/usr/bin
	cp bin/ferret deb/usr/bin/
	mkdir -p deb/DEBIAN
	cp src/resources/deb-package-conf deb/DEBIAN/control
	echo "Version: ${MAJOR_VERSION}.${MINOR_VERSION}" >> deb/DEBIAN/control
	dpkg -b deb ferret-lisp.deb
	rm -rf deb
	mv ferret-lisp.deb bin/
deb-repo: deb
	mkdir -p bin/debian-repo/conf/
	cp src/resources/deb-repo-conf bin/debian-repo/conf/distributions
	reprepro -b bin/debian-repo/ includedeb ferret-lisp bin/ferret-lisp.deb
docs:
	wget https://s3.amazonaws.com/ferret-lang.org/build-artifacts/org-mode-assets.zip
	unzip org-mode-assets.zip
	emacs -nw -Q --batch -l src/resources/tangle-docs
	mkdir -p docs/
	mv ferret-manual.html docs/
	rm org-mode-assets.zip
	mv org-mode-assets docs/ferret-styles
release: clean test-release packr deb-repo docs
	mkdir -p release/builds/
	mv bin/ferret* release/builds/
	cp release/builds/ferret.jar release/builds/ferret-`git rev-parse --short HEAD`.jar
	mv bin/debian-repo release/
	mv docs/* release/
	mv release/ferret-manual.html release/index.html
	rm -rf bin/ docs/
docker-release:
	 ${DOCKER_RUN} /bin/bash -c 'make release'
clean:
	rm -rf src/ bin/ docs/ org-mode-assets* release/
