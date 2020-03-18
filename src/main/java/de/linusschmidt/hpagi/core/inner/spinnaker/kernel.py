import pylab
import pyNN.spiNNaker as p

p.setup(timestep=1.0)

population1 = p.Population(1, p.IF_cond_exp, {}, label="population1")
input = p.Population(1, p.SpikeSourceArray, {"spike_times": [0]}, label="input")

input_proj = p.Projection(input, population1, p.OneToOneConnector(), p.StaticSynapse(weight=5.0, delay=1))

population1.record(['spikes', 'v'])

p.run(10)

spikes = population1.getSpikes()

v = population1.get_v()

time = [i[1] for i in v if i[0] == 0]
membrane_voltage = [i[2] for i in v if i[0] == 0]
pylab.plot(time, membrane_voltage)
pylab.xlabel("Time (ms)")
pylab.ylabel("Membrane Voltage")
pylab.axis([0, 10, -75, -45])
pylab.show()
