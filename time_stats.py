
import statistics

def parse_times(path):
    runtimes = []
    with open(path, 'r') as file:
        for line in file:
            mins_part = line.split('m')[0]
            seconds = line.split('m')[1].split('s')[0]
            seconds = seconds.replace(',', '.')
            runtimes.append(float(seconds.strip()) + 60 * float(mins_part.strip()))
    return runtimes
    

runtimes = parse_times('exec_times.txt')
serial_avg = 1923.227

avg = statistics.mean(runtimes)
std_dev = statistics.stdev(runtimes)
variance = statistics.variance(runtimes)
speed_up = serial_avg / avg

for i in range(len(runtimes)):
    print("Run ", i +1, " ", format(runtimes[i]).replace('.', ',') + "s")

print("Average: {:.3f}".format(avg).replace('.', ',') + "s")
print("Variance: {:.3f}".format(variance).replace('.', ','))
print("Standard Deviation: {:.3f}".format(std_dev).replace('.', ','))
print("Speed Up: {:.3f}".format(speed_up).replace('.', ','))