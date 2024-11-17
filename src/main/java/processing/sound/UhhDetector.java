package processing.sound;

import com.jsyn.ports.UnitOutputPort;
import processing.core.PApplet;;

public class UhhDetector extends Analyzer { //the uhhh detector
   private final com.jsyn.unitgen.PitchDetector detector;
   public float minimumConfidence = 0;

   public UhhDetector(PApplet var1, float var2) {
      super(var1);
      this.detector = new com.jsyn.unitgen.PitchDetector();
      this.minimumConfidence = var2;
   }

   public UhhDetector(PApplet var1) {
      this(var1, 0.8F);
   }

   protected void removeInput() {
      this.input = null;
   }

   protected void setInput(UnitOutputPort var1) {
      Engine.getEngine().add(this.detector);
      this.detector.start();
      this.detector.input.connect(var1);
   }

   public float analyze() {
      return this.analyze(this.minimumConfidence);
   }

   public float analyze(float var1) {
      return (float)(this.detector.confidence.getValue() >= (double)var1 ? this.detector.frequency.getValue() : 0.0);
   }

   public float analyze(float[] var1) {
      var1[0] = (float)this.detector.frequency.getValue();
      var1[1] = (float)this.detector.confidence.getValue();
      //return var1[1] >= this.minimumConfidence ? var1[0] : 0.0F;
      return var1[1] >= this.minimumConfidence ? var1[1] : 0.0F;
   }
}
