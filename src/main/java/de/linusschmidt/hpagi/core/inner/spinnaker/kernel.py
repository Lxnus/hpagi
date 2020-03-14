import nmpi

import pyNN.spiNNaker as pynn

client = nmpi.Client();

print(pynn.IF_cond_exp.default_parameters)

pynn.setup(0.1)

