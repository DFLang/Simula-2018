package simula.runtime.context;

public class FILE_ extends OBJECT {
	   // Declare parameters as attributes
	   public TEXTREF FILENAME_;
	   // Declare locals as attributes
	   public TEXTREF image;
	   protected boolean OPEN_;
	   public TEXTREF filename () {
		      return(ENVIRONMENT.copy(FILENAME_));
		   }
	   public boolean isopen () {
		      return(OPEN_);
		   }
	   public void setpos (int i) {
	      image . setpos(i);
	   }
	   public int pos () {
	      return(image . pos());
	   }
	   public boolean more () {
	      return(image . more());
	   }
	   public int length () {
	      return(image . length());
	   }
	   // Constructor
	   public FILE_(TEXTREF param_FILE_NAME) {
	      // Parameter assignment to locals
		   FILENAME_ = param_FILE_NAME;
	      // Class Body
		  if(FILENAME_ == null) {
		      ENVIRONMENT.error(new TEXTREF("Illegal File Name"));
		  }
	   }
	}
