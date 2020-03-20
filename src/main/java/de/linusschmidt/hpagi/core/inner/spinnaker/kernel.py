import pylab
import spynnaker8 as network

network.setup(timestep=1.0)

n_Neurons = 100

populationA = network.Population(n_Neurons, network.IF_cond_exp, {}, label="pop_A")
populationB = network.Population(n_Neurons, network.IF_cond_exp, {}, label="pop_B")

populationA.record("spikes")
populationB.record("spikes")

inputA = network.Population(n_Neurons, network.SpikeSourcePoisson(rate=10.0), {}, label="input_A")
inputB = network.Population(n_Neurons, network.SpikeSourcePoisson(rate=10.0), {}, label="input_B")

training = network.Population(n_Neurons, network.SpikeSourcePoisson(rate=10.0, start=2000.0), {}, label="training")

a_A_projection = network.Projection(inputA, populationA, network.OneToOneConnector(), synapse_type=network.StaticSynapse(weight=2.0, delay=1.0))
b_B_projection = network.Projection(inputB, populationB, network.OneToOneConnector(), synapse_type=network.StaticSynapse(weight=2.0, delay=10.0))

training_A_projection = network.Projection(training, populationA, network.OneToOneConnector())
training_B_projection = network.Projection(training, populationB, network.OneToOneConnector())

timing_rule = network.SpikePairRule()
weight_rule = network.AdditiveWeightDependence(w_max=5.0, w_min=0.0)

stdpModel = network.STDPMechanism(timing_dependence=timing_rule, weight_dependence=weight_rule)
stdpProjection = network.Projection(populationA, populationB, network.OneToOneConnector(), synapse_type=stdpModel)

network.run(5000)

spikesA = populationA.getSpikes()
spikesB = populationB.getSpikes()

print(stdpProjection.getWeights())

network.end()

pylab.figure()
pylab.xlim((0, 5000))
pylab.plot([i[1] for i in spikesA], [i[0] for i in spikesA], "r.")
pylab.plot([i[1] for i in spikesB], [i[0] for i in spikesB], "b.")
pylab.xlabel("Time/ms")
pylab.ylabel("Spike")
pylab.show()
