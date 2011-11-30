package org.fireflow.model.servicedef.impl;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.fireflow.model.data.Input;
import org.fireflow.model.data.Output;
import org.fireflow.model.servicedef.IOSpecification;


public class IOSpecificationImpl implements IOSpecification{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	List<Input> dataInputs = new ArrayList<Input>();
	List<Output> dataOutputs = new ArrayList<Output>();
	
		
	public List<Input> getInputs(){
		return dataInputs;
	}
	public List<Output> getOutputs(){
		return dataOutputs;
	}
	
	public void addInput(Input in){
		dataInputs.add(in);
	}
	
	public void addOutput(Output out){
		dataOutputs.add(out);
	}
	
	public Input getInput(String inputName){
		if (inputName==null || inputName.trim().equals("")){
			return null;
		}
		for (Input input : dataInputs){
			if (inputName.equals(input.getName())){
				return input;
			}
		}
		return null;
	}
	public Output getOutput(String outputName){
		if (outputName==null || outputName.trim().equals("")){
			return null;
		}
		
		for (Output output : dataOutputs){
			if (outputName.equals(output.getName())){
				return output;
			}
		}
		return null;
	}
}
