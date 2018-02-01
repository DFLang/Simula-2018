package simula.test.context;

public class Simset //extends OBJECT
{
	   // Declare parameters as attributes
	   // Declare locals as attributes
	   public class linkage //extends OBJECT
	   {
	      // Declare parameters as attributes
	      // Declare locals as attributes
	      public linkage SUC;
	      public linkage PRED;
	      public link suc () {
	         return((SUC instanceof link)?(((link)(SUC))):(null));
	      }
	      public link pred () {
	         return((PRED instanceof link)?(((link)(PRED))):(null));
	      }
	      // Constructor
	      public linkage() {
	         // Parameter assignment to locals
	         // Class Body
	      }
	   }
	   public class head extends linkage {
	      // Declare parameters as attributes
	      // Declare locals as attributes
	      public link first () {
	         return(suc());
	      }
	      public link last () {
	         return(pred());
	      }
	      public boolean empty () {
	         return(SUC == (linkage)this);
	      }
	      public int cardinal () {
	         {
	            int I=0;
	            linkage X;
	            X = (linkage)this;
	            for(X=X . suc();X != null;) {
	               I = I + 1;
	            }
	            return(I);
	         }
	      }
	      public void clear () {
	         {
	            link X;
	            for(X=first();X != null;) {
	               X . out();
	            }
	         }
	      }
	      // Constructor
	      public head() {
	         // Parameter assignment to locals
	         // Class Body
	         SUC = PRED = (linkage)this;
	      }
	   }
	   public class link extends linkage {
	      // Declare parameters as attributes
	      // Declare locals as attributes
	      public void out () {
	         if(SUC != null) {
	            {
	               SUC . PRED = PRED;
	               PRED . SUC = SUC;
	               SUC = PRED = null;
	            }
	         }
	      }
	      public void follow (linkage X) {
	         {
	            out();
	            if(X != null) {
	               {
	                  if(X . SUC != null) {
	                     {
	                        PRED = X;
	                        SUC = X . SUC;
	                        SUC . PRED = X . SUC = (linkage)this;
	                     }
	                  }
	               }
	            }
	         }
	      }
	      public void precede (linkage X) {
	         {
	            out();
	            if(X != null) {
	               {
	                  if(X . SUC != null) {
	                     {
	                        SUC = X;
	                        PRED = X . PRED;
	                        PRED . SUC = X . PRED = (linkage)this;
	                     }
	                  }
	               }
	            }
	         }
	      }
	      public void into (head S) {
	         precede(S);
	      }
	      // Constructor
	      public link() {
	         // Parameter assignment to locals
	         // Class Body
	      }
	   }
	   // Constructor
	   public Simset() {
	      // Parameter assignment to locals
	      // Class Body
	   }
	}