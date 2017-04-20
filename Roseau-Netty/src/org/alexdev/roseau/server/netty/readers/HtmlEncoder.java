package org.alexdev.roseau.server.netty.readers;

public final class HtmlEncoder {
	  private HtmlEncoder() {}

	  public static <T extends Appendable> T escapeNonLatin(CharSequence sequence,
	      T out) throws java.io.IOException {
	    for (int i = 0; i < sequence.length(); i++) {
	      char ch = sequence.charAt(i);
	      if (Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.BASIC_LATIN) {
	        out.append(ch);
	      } else {
	        int codepoint = Character.codePointAt(sequence, i);
	        // handle supplementary range chars
	        i += Character.charCount(codepoint) - 1;
	        // emit entity
	        out.append("&#x");
	        out.append(Integer.toHexString(codepoint));
	        out.append(";");
	      }
	    }
	    return out;
	  }
	}
