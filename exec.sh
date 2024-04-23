#!/bin/bash

input_file="inputs/input5.txt"
iterations="100000"
num_threads="4"
output_file="test.txt"
exec_times_file="exec_times.txt"

for((i=0; i<5; i++))
do
    { time ./BarnesHut "$input_file" "$iterations" "$num_threads" "$output_file" ;} > >(tee -a stdout.txt) 2>&1 | grep real | awk '{print $2}' >> "$exec_times_file" & wait; 
done