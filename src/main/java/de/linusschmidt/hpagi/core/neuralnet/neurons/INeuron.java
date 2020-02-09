package de.linusschmidt.hpagi.core.neuralnet.neurons;

import de.linusschmidt.hpagi.core.neuralnet.utilities.Connection;

public interface INeuron {

    /**
     * Author: Linus Schmidt!
     * All rights reserved!
     */
	
	void setValue(double value);
	double getValue();
	void reset();
	void addConnection(Connection connection);
}
