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