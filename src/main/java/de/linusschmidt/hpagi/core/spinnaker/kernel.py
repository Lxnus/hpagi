import matplotlib.pyplot as plt
import pyNN.utility.plotting as plot
import pyNN.spiNNaker as network

from spynnaker_external_devices_plugin.pyNN.connections.spynnaker_live_spikes_connection import \
    SpynnakerLiveSpikesConnection

network.setup(timestep=1.0)

n_Neurons = 100
simulationTime = 1000

populationA = network.Population(n_Neurons, network.IF_curr_exp(), label="popA")
populationB = network.Population(n_Neurons, network.IF_curr_exp(), label="popB")

populationA.record("spikes")
populationB.record("spikes")

inputA = network.Population(n_Neurons, network.SpikeSourcePoisson(rate=10.0), label="inputA")
inputB = network.Population(n_Neurons, network.SpikeSourcePoisson(rate=10.0), label="inputB")

training = network.Population(n_Neurons, network.SpikeSourcePoisson(rate=10.0, start=200.0, duration=500.0),
                              label="training")

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

import spynnaker_external_devices_plugin.pyNN as ExternalDevices

ExternalDevices.activate_live_output_for(population=populationA, host="45.81.233.16")
ExternalDevices.activate_live_output_for(population=populationB, host="45.81.233.16")


def send_spike(label, sender):
    sender.send_spike(label, 0, send_full_keys=True)


live_spikes_connection = SpynnakerLiveSpikesConnection(send_labels=["spike_sender"])
live_spikes_connection.add_start_callback("spike_sender", send_spike)

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
