FROM    ubuntu:18.04
MAINTAINER Nurullah Akkaya <nurullah@nakkaya.com>

RUN apt-get update && \
    apt-get upgrade -y

# Setup Tangle Requirements

RUN apt-get install wget make git emacs25-nox openjdk-8-jre-headless -y

RUN wget https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein
RUN chmod +x lein
RUN mv lein /usr/bin/
RUN lein

ENV LEIN_ROOT true

# Setup Test Requirements

# arduino
RUN apt-get install xz-utils
RUN wget http://downloads.arduino.cc/arduino-1.8.7-linux64.tar.xz
RUN tar xf arduino-1.8.7-linux64.tar.xz
RUN mv arduino-1.8.7 /usr/local/share/arduino
RUN ln -s /usr/local/share/arduino/arduino /usr/local/bin/arduino
RUN ln -s /usr/local/share/arduino/arduino-builder /usr/local/bin/arduino-builder
RUN rm -rf arduino-1.8.7-linux64.tar.xz

# x86
RUN apt-get install gcc-5 g++-5 cppcheck -y
RUN update-alternatives --install /usr/bin/g++ g++ /usr/bin/g++-5 50
RUN apt-get install clang-6.0 llvm-6.0 -y
RUN update-alternatives --install /usr/bin/clang++ clang++ /usr/bin/clang++-6.0 50

# cppcheck
RUN apt-get -y install unzip libpcre3-dev
RUN wget https://github.com/danmar/cppcheck/archive/1.87.zip
RUN unzip 1.87.zip
RUN cd cppcheck-1.87/ && make SRCDIR=build CFGDIR=/usr/share/cppcheck/ HAVE_RULES=yes CXXFLAGS="-O2 -DNDEBUG -Wall -Wno-sign-compare -Wno-unused-function"
RUN cd cppcheck-1.87/ && make install CFGDIR=/usr/share/cppcheck/
RUN rm -rf cppcheck-1.87 1.87.zip

# Setup Platform Build Requirements
RUN apt-get install zip reprepro -y

# Setup Platform Env Requirements
RUN apt-get install valgrind -y

RUN echo 'export PS1="\e[0;31mλ \e[m"' >> /root/.bashrc
