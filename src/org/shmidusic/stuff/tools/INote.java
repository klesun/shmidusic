package org.shmidusic.stuff.tools;

import org.shmidusic.sheet_music.staff.staff_config.KeySignature;
import org.shmidusic.stuff.graphics.ImageStorage;
import org.apache.commons.math3.fraction.Fraction;

import java.util.Arrays;

public interface INote extends ISound
{
	Fraction getLength();

	default Boolean isTooLong() {
		return isTooLong(getLength());
	}

	default Boolean isTooShort() {
		return isTooShort(getLength());
	}

	default Boolean isEbony() { return isEbony(getTune()); }

	default int ivoryIndex(KeySignature siga) { return ivoryIndex(getTune(), siga); }

	static int ivoryIndex(int tune) { return ivoryIndex(tune, new KeySignature(0)); }

	static int ivoryIndex(int tune, KeySignature siga) {
		return getOctave(tune) * 7 + siga.calcIvoryMask(tune);
	}

	/** @return tune */
	static int fromIvory(int ivory) {
		return (ivory / 7) * 12 + Arrays.asList(0,2,4,5,7,9,11).get(ivory % 7);
	}

	static int getOctave(int tune) { return tune /12; }

	// 1/256 + 1/128 + 1/64 + 1/32 + 1/16 + 1/8 + 1/4 + 1/2 = 1111 1111

	// 0000 0111 - true

	// 0001 1111 - true

	// 0011 1111 - true

	// 0010 0110 - false

	/** @return true if length can be expressed through sum 2 + 1 + 1/2 + 1/4 + 1/8 + ... 1/2^n */
	static Boolean isDotable(Fraction length) {

		if (isTooShort(length) || isTooLong(length) || length.getDenominator() > ImageStorage.getShortLimit().getDenominator()) {
			return false;
		} else {
			length = length.divide(ImageStorage.getTallLimit().multiply(2)); // to make sure, that the length is less than 1
			if (length.compareTo(new Fraction(1)) >= 0) {
				Logger.fatal("Providen fraction is greater than 1 even after deviding it to gretest possible note length! We'll all die!!!" + length);
			}

			if (Fp.isPowerOf2(length.getDenominator())) { // will be false for triols
				return Fp.isPowerOf2(length.getNumerator() + 1); // not sure... but, ok, kinda sure
			} else {
				return false;
			}
		}
	}

	static Boolean isTooLong(Fraction length) {
		return length.compareTo(ImageStorage.getTallLimit()) > 0;
	}

	static Boolean isTooShort(Fraction length) {
		return length.compareTo(ImageStorage.getShortLimit()) < 0;
	}

	static Boolean isEbony(int tune) {
		return Arrays.asList(1, 3, 6, 8, 10).contains(tune % 12);
	}

	static int nextIvoryTune(int tune) {
		return isEbony(tune + 1) ? tune + 2 : tune + 1;
	}

	static int prevIvoryTune(int tune) {
		return isEbony(tune - 1) ? tune - 2 : tune - 1;
	}

	// TODO: what if i said we could store these instead of numbers in json?
	static String strIdx(int n){ return Arrays.asList("do","re","mi","fa","so","la","ti").get(n % 12); }
}
