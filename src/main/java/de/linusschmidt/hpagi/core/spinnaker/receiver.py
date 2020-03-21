from spynnaker_external_devices_plugin.pyNN.connections.spynnaker_live_spikes_connection import \
    SpynnakerLiveSpikesConnection


def recieve_spikes(label, time, neuron_ids):
    for neuron_id in neuron_ids:
        print("Received spike at time {} from {}-{}".format(time, label, neuron_id))


live_spike_connection = SpynnakerLiveSpikesConnection(receive_labels=["receiver"])
live_spike_connection.add_receive_callback("receiver", recieve_spikes)
