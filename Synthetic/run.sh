#!/bin/sh
clear
clear
Java_files="*.java"
GCC_files="*.c"

cd "C_code/"
echo "Compiling C++ code..."
for file in $GCC_files
do
  echo "Compiling $file..."
  gcc "$file"
done

cd "../Java_code"
echo "Compiling Java code..."
for file in $Java_files
do
  echo "Compiling $file..."
  javac "$file"
done


input="../Inputs/input"
output="../Outputs/output"
heading=360
java Main $input $output $heading


cd "../C_code"
lat_min=30
lat_max=30
lon_min=30
lon_max=30
pixels_W=1149
pixels_H=751
./a.out $lat_min $lat_max $lon_min $lon_max $pixels_W $pixels_H