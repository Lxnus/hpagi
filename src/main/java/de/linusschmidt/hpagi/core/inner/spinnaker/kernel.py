import matplotlib.pyplot as plt
import pyNN.utility.plotting as plot
import spynnaker8 as network

network.setup(timestep=1.0)

n_Neurons = 100
simulationTime = 10000

populationA = network.Population(n_Neurons, network.IF_curr_exp(), label="popA")
populationB = network.Population(n_Neurons, network.IF_curr_exp(), label="popB")

populationA.record("spikes")
populationB.record("spikes")

inputA = network.Population(n_Neurons, network.SpikeSourcePoisson(rate=10.0), label="inputA")
inputB = network.Population(n_Neurons, network.SpikeSourcePoisson(rate=10.0), label="inputB")

training = network.Population(n_Neurons, network.SpikeSourcePoisson(rate=10.0, start=1500.0, duration=6000.0), label="training")

network.Projection(inputA, populationA, network.OneToOneConnector(),
                                    synapse_type=network.StaticSynapse(weight=2.0))
network.Projection(inputB, populationB, network.OneToOneConnector(),
                                    synapse_type=network.StaticSynapse(weight=2.0))

network.Projection(training, populationA, network.OneToOneConnector(),
                                           synapse_type=network.StaticSynapse(weight=5.0, delay=1.0))
network.Projection(training, populationB, network.OneToOneConnector(),
                                           synapse_type=network.StaticSynapse(weight=5.0, delay=10.0))

timing_rule = network.SpikePairRule(tau_plus=20.0, tau_minus=20.0, A_plus=0.5, A_minus=0.5)
weight_rule = network.AdditiveWeightDependence(w_max=5.0, w_min=0.0)

stdpModel = network.STDPMechanism(timing_dependence=timing_rule, weight_dependence=weight_rule, weight=0.0, delay=5.0)
stdpProjection = network.Projection(populationA, populationB, network.OneToOneConnector(), synapse_type=stdpModel)

network.run(simulationTime)

neoA = populationA.get_data(variables=["spikes"])
spikesA = neoA.segments[0].spiketrains

neoB = populationB.get_data(variables=["spikes"])
spikesB = neoB.segments[0].spiketrains

print(stdpProjection.getWeights())

network.end()

line_properties = [{'color': 'red', 'markersize': 5},
                   {'color': 'blue', 'markersize': 2}]

figure = plot.Figure(plot.Panel(spikesA,
                                spikesB,
                                yticks=True,
                                xlim=(0, simulationTime),
                                line_properties=line_properties),
                     title="Network",
                     annotations="Simulated with: {}".format(network.name()))

plt.show()

figure.save("outputFile.png")