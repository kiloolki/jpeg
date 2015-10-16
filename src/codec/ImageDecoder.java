package codec;

import java.awt.Color;
import java.util.Map;
import java.util.Map.Entry;

public class ImageDecoder {

	private int larghezza, altezza;
	private String data;
	private Map<Object, String> prefixCodes;

	public ImageDecoder(int larg, int alt, String data,
			Map<Object, String> prefixCodes) {
		this.larghezza = larg;
		this.altezza = alt;
		this.data = data;
		this.prefixCodes = prefixCodes;
	}

	public int[] decodifica() {
		int larghezza = this.getLarghezza();
		int altezza = this.getAltezza();
		int numPixels = larghezza * altezza;
		int[][] blocks = new int[3][numPixels];
		int pointer = 0; // char index at data
		for (int component = 0; component < blocks.length; component++) {
			for (int x = 0; x < larghezza; x += 8) {
				for (int y = 0; y < numPixels; y += 8 * larghezza) {
					int[] block = new int[64];
					// scansione a zig zag collocando i coefficiente
					//nella posizione giusta 
					int k = 0, runlength = 0, coefficient = 0;
					boolean done = false;
					for (int i = 0; i < 15 && !done; i++) {
						int m = i % 8;
						for (int j = 0; j < ((i / 8 > 0) ? 8 - m - 1 : m + 1); j++) {
							if (j != 0)
								k += 7 * ((i % 2 > 0) ? 1 : -1);
							if (runlength > 0)
								runlength--;
							else {
								// prossimo simbolo dai dati
								String prefixCode = this
										.getNextPrefixCode(pointer);
								String nextSymbol = this.getSymbol(prefixCode);
								pointer += prefixCode.length();
								// verifica condizione d'arresto:EOB
								if (nextSymbol.equals(String.valueOf(Constants
										.getEob()))) {
									done = true;
									break;
								} else {
									// altrimenti, continua con il run-length
									runlength = Integer.parseInt(nextSymbol);
									prefixCode = this
											.getNextPrefixCode(pointer);
									nextSymbol = this.getSymbol(prefixCode);
									coefficient = Integer.parseInt(nextSymbol);
									pointer += prefixCode.length();
								}
							}
							if (runlength == 0)
								block[k] = coefficient;
						}
						if ((i + 1) / 8 > 0)
							k += ((i % 2 > 0) ? 1 : 8);
						else
							k += ((i % 2 > 0) ? 8 : 1);
					}
					// andiamo ad effettuare la quantizzazione
					for (int i = 0; i < block.length; i++){
						
						
						
						switch (component) {
						case 0:
							block[i] *= Constants.getQuantizationValues_Y()[i];
							break;
						case 1:
							block[i] *= Constants.getQuantizationValues_CbCr()[i];
							break;
						case 2:
							block[i] *= Constants.getQuantizationValues_CbCr()[i];
							break;
						}
						
					}
					// calcoliamo  IDCT
					double[] idct = this.computeIDCT(block);
					// arrotondiamo al più piccolo intero e incrementiamo di 128
					for (int i = 0; i < block.length; i++)
						block[i] = 128 + (int) Math.round(idct[i]);
					//mettiamo tutto al posto giusto
					for (int i = 0; i < block.length; i++)
						blocks[component][x + y + larghezza * (i / 8) + (i % 8)] = block[i];
				}
			}
		}
		// convertiamo in rgb
		int[] rgbArray = new int[numPixels];
		for (int i = 0; i < rgbArray.length; i++)
			rgbArray[i] = this.convertYcbCrToRgb(blocks[0][i], blocks[1][i],
					blocks[2][i]);
		return rgbArray;
	}

	public int convertYcbCrToRgb(int y, int cb, int cr) {
		int[] rgb = new int[] { y + Math.round(1.402f * (cr - 128)),
				Math.round(y - .34414f * (cb - 128) - .71414f * (cr - 128)),
				Math.round(y + 1.772f * (cb - 128)) };
		for (int i = 0; i < rgb.length; i++)
			if (rgb[i] < 0)
				rgb[i] = 0;
			else if (rgb[i] > 255)
				rgb[i] = 255;
		return new Color(rgb[0], rgb[1], rgb[2]).getRGB();
	}

	public double[] computeIDCT(int[] block) {
		double[] idct = new double[block.length];
		for (int i = 0; i < idct.length; i++) {
			for (int j = 0; j < block.length; j++) {
				int u = j % 8;
				int v = j / 8;
				idct[i] += block[j]
						* Math.cos((Math.PI * (1 + 2 * (i % 8)) * u) / 16)
						* Math.cos((Math.PI * (1 + 2 * (i / 8)) * v) / 16)
						* .25 * ((u == 0) ? 1 / Math.sqrt(2) : 1)
						* ((v == 0) ? 1 / Math.sqrt(2) : 1);
			}
		}
		return idct;
	}

	public String getNextPrefixCode(int pointer) {
		String prefix = "";
		Map<Object, String> prefixCodes = this.getPrefixCodes();
		while (!prefixCodes.containsValue(prefix))
			prefix += this.getData().charAt(pointer++);
		return prefix;
	}

	public String getSymbol(String prefixCode) {
		for (Entry<Object, String> entry : prefixCodes.entrySet())
			if (prefixCode.equals(entry.getValue()))
				return entry.getKey().toString();
		return null;
	}

	public int getLarghezza() {
		return larghezza;
	}

	public void setLarghezza(int larg) {
		this.larghezza = larg;
	}

	public int getAltezza() {
		return altezza;
	}

	public void setAltezza(int alt) {
		this.altezza = alt;
	}

	public Map<Object, String> getPrefixCodes() {
		return prefixCodes;
	}

	public void setPrefixCodes(Map<Object, String> prefixCodes) {
		this.prefixCodes = prefixCodes;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

}
