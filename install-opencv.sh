if [ ! -d "opencv-build-3.1.0/bin" ]; then
  git clone https://github.com/Itseez/opencv.git
  cd opencv
  git checkout tags/3.1.0
  cd ..
  mkdir opencv-build-3.1.0
  cd opencv-build-3.1.0
  cmake ../opencv
  make -j2
  cd ..
fi

mkdir vision/lib
cp opencv-build-3.1.0/lib/libopencv_java310.so vision/lib/libopencv_java310.so
