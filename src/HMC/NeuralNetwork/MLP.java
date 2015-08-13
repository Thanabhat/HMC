package HMC.NeuralNetwork;

import java.util.Random;

public class MLP {
	public int N;
	public int n_ins;
	public int[] hidden_layer_sizes;
	public int n_outs;
	public int n_layers;
	public HiddenLayer[] hiddenLayers;
	public LogisticRegression logRegressionLayer;
	public Random rng;
	
	public MLP(int N, int n_ins, int[] hidden_layer_sizes, int n_outs, Random rng){
		this.N = N;
		this.n_ins = n_ins;
		this.hidden_layer_sizes = hidden_layer_sizes;
		this.n_outs = n_outs;
		this.n_layers = this.hidden_layer_sizes.length;

		this.hiddenLayers = new HiddenLayer[this.n_layers];

		if(rng == null)	this.rng = new Random(1234);
		else this.rng = rng;
		
		int input_size;
		for(int i=0; i<this.n_layers;i++){
			if(i==0){
				input_size = this.n_ins;
			}else{
				input_size = this.hidden_layer_sizes[i-1];
			}
			
			this.hiddenLayers[i] = new HiddenLayer(this.N, input_size, this.hidden_layer_sizes[i], null, null, rng);
		}
		
		this.logRegressionLayer = new LogisticRegression(this.N, this.hidden_layer_sizes[this.n_layers-1], n_outs);
	}

	public void train(int[][] train_X, int[][] train_Y, double lr, double l2_reg, int epochs){
		
	}
	
	public void predict(){
		
	}
}
