package com.logicoverflow.fit_bot.Util;

/*
 MIT license, do whatever

 Copyright (c) 2017 Matthias Schicker

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */



import android.animation.Animator;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * <p>
 * Provides a simple, promise-like, interface to create complex
 * animations, easily extensible with callbacks and hooks all
 * along the way.
 * </p>
 *
 * Exemplary usage to create a simple wiggle animation:
 * <pre>
 *      AndroidAnimationBuilder builder = new AndroidAnimationBuilder(v);
 *      builder.setDefaultStepLength(60)
 *             .rotateTo(2)
 *             .then().rotateBy(-6)
 *             .then().rotateBy(7)
 *             .then().reset().ms(120)
 *             .execute();
 * </pre>
 *
 * @author  Created by Matthias Schicker (KoMaXX) on 28/02/2017.
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AndroidAnimationBuilder {
    private static final boolean DEBUG_LOGGING = false;

    private final WeakReference<View> viewRef;

    private boolean allowLayerAdjustmentForAnimation = true;
    private int defaultStepDurationMS = 300;
    private boolean startClean = false;

    private final ArrayList<AnimationStep> steps = new ArrayList<>();

    // not yet added to steps!
    private @NonNull
    AnimationStep currentStep = new AnimationStep();

    private boolean executionTriggered = false;

    private boolean autoCancelWithTag = true;


    /**
     * Starting point for all the animation fun. Will take any ol' view.
     *
     * @param v The view on which the animations will be executed. WEAKLY held!
     */
    public AndroidAnimationBuilder(View v) {
        viewRef = new WeakReference<>(v);
    }

    /**
     * Set this to define the default duration of all steps, i.e., the
     * duration of all steps that did not receive explicit duration
     * definitions.<br/>
     * Defaults to 300ms.
     */
    public AndroidAnimationBuilder setDefaultStepDuration(int ms) {
        if (alreadyExecuted()) return this;

        defaultStepDurationMS = ms;
        return this;
    }

    /**
     * Pre-executes a CLEAN step before starting any animations.
     * Applied before calculating the state to return to when running
     * a 'reset' step, so 'reset' will always run to the CLEAN step for
     * sure. <br/>
     * Valuable when an animation is triggered by user action on a view
     * that is already animating. Will ensure that the new animation
     * simply replaces the old one - instead of piling on.
     */
    public AndroidAnimationBuilder startClean(boolean startClean){
        if (alreadyExecuted()) return this;

        this.startClean = startClean;
        return this;
    }

    /**
     * If this is set true, the running animation steps will automatically
     * realize that another AnimationBuilder animation was assigned
     * to the view and cancel itself.
     * <br/>
     * <b>Note</b>: This sets the view's tag. Do not use when you need the
     * tag yourself!
     * <br/>
     * Defaults to {@code true}
     */
    public AndroidAnimationBuilder setAutoCancelWithTag(boolean autoCancelWithTag) {
        if (alreadyExecuted()) return this;

        this.autoCancelWithTag = autoCancelWithTag;
        return this;
    }

    /**
     * If <code>true</code>, the AnimationBuilder will attempt to set the
     * layer type of the view for the duration of the animation.
     * Default: <code>true</code>.
     */
    public AndroidAnimationBuilder setAllowLayerAdjustmentForAnimation(boolean allowLayerAdjustmentForAnimation) {
        if (alreadyExecuted()) return this;

        this.allowLayerAdjustmentForAnimation = allowLayerAdjustmentForAnimation;
        return this;
    }

    /**
     * Add a rotation animation to the current step. Will replace previously
     * set rotationBy definitions for the current step.
     * <br/><br/>
     * <b>NOTE</b>: Rotations defined as rotateTo have precedence over
     * rotateBy definitions. If both are defined, ONLY the rotateTo will
     * be executed.
     */
    public AndroidAnimationBuilder rotateBy(float degrees) {
        if (alreadyExecuted()) return this;

        currentStep.setRotateBy(degrees);
        return this;
    }

    /**
     * Add a rotation animation to the current step. Will replace previously
     * set rotationTo definitions for the current step.
     * <br/><br/>
     * <b>NOTE</b>: Rotations defined as rotateTo have precedence over
     * rotateBy definitions. If both are defined, ONLY the rotateTo will
     * be executed.
     */
    public AndroidAnimationBuilder rotateTo(float degrees) {
        if (alreadyExecuted()) return this;

        currentStep.setRotateTo(degrees);
        return this;
    }

    /**
     * Add a translation on the xAxis to the current animation step.
     * Will replace current x-translation definitions for the current step.
     */
    public AndroidAnimationBuilder translateX(float xTransPx){
        if (alreadyExecuted()) return this;

        currentStep.setTranslateX(xTransPx);
        return this;
    }

    /**
     * Add a translation on the yAxis to the current animation step.
     * Will replace current y-translation definitions for the current step.
     */
    public AndroidAnimationBuilder translateY(float yTransPx){
        if (alreadyExecuted()) return this;

        currentStep.setTranslateY(yTransPx);
        return this;
    }

    /**
     * Add a translation on the zAxis to the current animation step.
     * Will replace current z-translation definitions for the current step.
     *
     * ONLY available in API level >=21 environments. No-op otherwise.
     */
    public AndroidAnimationBuilder translateZ(float zTransPx){
        if (alreadyExecuted()) return this;

        currentStep.setTranslateZ(zTransPx);
        return this;
    }

    /**
     * Add scaling in x direction to the current animation step.
     * Will replace current x-scale definitions for the current step.
     */
    public AndroidAnimationBuilder scaleX(float scaleX){
        if (alreadyExecuted()) return this;

        currentStep.setScaleX(scaleX);
        return this;
    }

    /**
     * Add scaling in y direction to the current animation step.
     * Will replace current y-scale definitions for the current step.
     */
    public AndroidAnimationBuilder scaleY(float scaleY){
        if (alreadyExecuted()) return this;

        currentStep.setScaleY(scaleY);
        return this;
    }

    /**
     * Add alpha animation to the current step.
     */
    public AndroidAnimationBuilder alpha(float alpha){
        if (alreadyExecuted()) return this;

        currentStep.setAlpha(alpha);
        return this;
    }

    /**
     * <p>
     * Overwrites all previous set step definitions for translation,
     * rotation, scaling and alpha.
     * </p>
     * <p>
     * A meta definition that animates towards a 'clean' state,
     * i.e., a state with no scaling, no translation, no rotation,
     * and an alpha value of 1. If not all of those resets are
     * desired, overwrite them afterwards for this animation step.
     * </p>
     * <p>
     * Also see the predefined {@link AnimationStepHook} {@code CLEAN}
     * that does basically the same thing in a single step (not animated)
     * </p>
     */
    public AndroidAnimationBuilder clean(){
        if (alreadyExecuted()) return this;

        currentStep.setAlpha(1);
        currentStep.setRotateTo(0f);
        currentStep.setScaleX(1);
        currentStep.setScaleY(1);
        currentStep.setTranslateX(0);
        currentStep.setTranslateY(0);
        currentStep.setTranslateZ(0);

        return this;
    }

    /**
     * Add some action that is to be executed at the beginning
     * of the animation step. Called in main thread.
     */
    public AndroidAnimationBuilder run(AnimationStepHook toRun){
        if (alreadyExecuted()) return this;
        currentStep.setPreStep(toRun);
        return this;
    }

    /**
     * Add some action that is to be executed at the END
     * of the animation step, after the animation stopped. Called in main thread.
     */
    public AndroidAnimationBuilder runAfter(AnimationStepHook toRun){
        if (alreadyExecuted()) return this;
        currentStep.setPostStep(toRun);
        return this;
    }

    /**
     * Finishes the current animation step definition and starts the next one.
     * Unless given a specific duration it will have the default duration.
     * An empty step will simply appear as a pause. An empty step at the end
     * of the sequence will be ignored.
     */
    public AndroidAnimationBuilder then() {
        if (alreadyExecuted()) return this;

        steps.add(currentStep);
        currentStep = new AnimationStep();
        return this;
    }

    /**
     * Call this to insert a pause between two animation steps.
     * Do NOT call then() afterwards as that will be done for you.
     *
     * NOTE: If the current step is empty, it will *NOT* be finished
     * beforehand but simply assigned the ms as duration and then finished.
     *
     * This is to avoid undesired behavior in form of an unexpectedly longer
     * pause when calling [..].then().pause()
     */
    public AndroidAnimationBuilder pause(int ms){
        if (!currentStep.isEmpty()){
            then();
        }
        ms(ms);
        then();
        return this;
    }

    /**
     * Duplicates the current step x times. Afterwards the builder
     * is in the last copy of the repeated step. You might want
     * to call 'then' afterwards to get a fresh step ;)
     *
     * @param times How often the current step is to be repeated.<br/>
     *              <=0: Does nothing<br/>
     *              1: The current step will be executed two times<br/>
     *              n: The current step will be executed (n+1) times overall
     */
    public AndroidAnimationBuilder repeat(int times){
        if (times <= 0) return this;
        if (alreadyExecuted()) return this;

        for (int i = 1; i < times; i++){
            steps.add(new AnimationStep(currentStep));
        }

        return this;
    }

    private boolean alreadyExecuted() {
        if (executionTriggered){
            Log.w("AndroidAnimationBuilder", "Further animation definitions ignored: Execution already started!");
        }
        return executionTriggered;
    }

    /**
     * Set the duration for the current step. If not set, the step will use the
     * default duration. Values <1 unset any previously set duration.
     */
    public AndroidAnimationBuilder ms(int ms) {
        currentStep.durationMs = ms;
        return this;
    }

    /**
     * Sets multiple values in this step in such a way that all modifications up
     * to this point are undone (translated back to original position, unscaled,
     * rotated back to original orientation, alpha = startAlpha).
     *
     * May be combined with any other step definition.
     */
    public AndroidAnimationBuilder reset() {
        if (alreadyExecuted()) return this;

        currentStep.setResetting(true);

        return this;
    }

    /**
     * Assigns a deceleration interpolator to the animation step, replacing any
     * previously defined interpolators.
     * <br/>
     * <b>Default</b> interpolator: EaseInEaseOut
     */
    public AndroidAnimationBuilder decelerate() {
        if (alreadyExecuted()) return this;
        currentStep.setInterpolator(new DecelerateInterpolator());
        return this;
    }

    /**
     * Assigns an acceleration interpolator to the animation step, replacing any
     * previously defined interpolators.
     * <br/>
     * <b>Default</b> interpolator: AccelerateDecelerate
     */
    public AndroidAnimationBuilder accelerate() {
        if (alreadyExecuted()) return this;
        currentStep.setInterpolator(new AccelerateInterpolator());
        return this;
    }

    /**
     * MUST be the final call to the builder. Compiles the actual animations
     * out of the definitions.
     * All following calls to the build will have no effect;
     */
    public void execute() {
        if (alreadyExecuted()) return;
        executionTriggered = true;

        // prepare the chain:
        // add the current step
        if (!currentStep.isEmpty()) steps.add(currentStep);
        if (steps.size() < 1){
            if (DEBUG_LOGGING){
                Log.w("AndroidAnimationBuilder", "No animation defined.");
            }
            return;
        }

        View view = viewRef.get();
        if (view != null && startClean){
            CLEAN.run(view);
        }

        // build startState to enable 'reset'
        StartState startState = new StartState(view);

        // build final step that reverts layer changes.
        FinalStep finalStep = new FinalStep();
        finalStep.viewRef = viewRef;
        steps.add(finalStep);

        String tag = null;
        if (view != null && autoCancelWithTag){
            tag = this.toString();
            view.setTag(tag);
        }

        // build chain out of animation steps. Set defaults if not done yet
        for (int i = 0; i < steps.size()-1; i++){
            AnimationStep step = steps.get(i);
            step.viewRef = viewRef;
            step.nextStep = steps.get(i+1);
            step.referencingTag = tag;
            step.startState = startState;
            step.setDurationIfUnset(defaultStepDurationMS);
        }

        if (view != null && allowLayerAdjustmentForAnimation) {
            finalStep.setEndLayerType(view.getLayerType());

            // speed up the animation if available
            view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }

        steps.get(0).execute();
    }

    static class AnimationStep implements Animator.AnimatorListener {
        boolean resetting;

        @Nullable Float rotateByDegrees;
        @Nullable Float rotateToDegrees;

        @Nullable Float translationX;
        @Nullable Float translationY;
        @Nullable Float translationZ;

        @Nullable Float scaleX;
        @Nullable Float scaleY;

        @Nullable Float alpha;

        @Nullable
        AnimationStepHook preStep;
        @Nullable AnimationStepHook postStep;

        /**
         * Change the interpolation behavior. Only affects actual animations.
         * Optional, defaults to EaseInEaseOut.
         */
        @Nullable Interpolator interpolator;

        // set at latest when animation is built
        int durationMs;

        // set when animation is built
        WeakReference<View> viewRef;
        StartState startState;
        AnimationStep nextStep;
        /**
         * Used for automatic animation abortion when another AnimationBuilder is
         * executed on the same view.
         */
        String referencingTag;

        /**
         * Some old devices will call onAnimationEnd more than once! This field
         * ensures that post-steps and further steps will be run only once.
         */
        boolean stepAlreadyFinished = false;
        /**
         * Used to remember that an animation was canceled and abort in
         * onAnimationEnded.
         */
        boolean canceled = false;

        public AnimationStep(){ }

        public AnimationStep(AnimationStep from) {
            this.resetting = from.resetting;

            this.rotateByDegrees = from.rotateByDegrees;
            this.rotateToDegrees = from.rotateToDegrees;

            this.translationX = from.translationX;
            this.translationY = from.translationY;
            this.translationZ = from.translationZ;

            this.scaleX = from.scaleX;
            this.scaleY = from.scaleY;

            this.alpha = from.alpha;

            this.preStep = from.preStep;
            this.postStep = from.postStep;

            this.interpolator = from.interpolator;
        }


        public void setRotateTo(Float rotateToDegrees) {
            this.rotateToDegrees = rotateToDegrees;
        }
        void setRotateBy(float degrees) {
            rotateByDegrees = degrees;
        }

        void setTranslateX(float translateX) {
            this.translationX = translateX;
        }
        void setTranslateY(float translateY) {
            this.translationY = translateY;
        }
        void setTranslateZ(float translateZ) {
            this.translationZ = translateZ;
        }

        void setScaleX(float scaleX) {
            this.scaleX = scaleX;
        }
        void setScaleY(float scaleY) {
            this.scaleY = scaleY;
        }

        void setAlpha(float alpha){
            this.alpha = alpha;
        }

        void setPreStep(@Nullable AnimationStepHook toRun){
            this.preStep = toRun;
        }

        public void setPostStep(@Nullable AnimationStepHook toRun) {
            this.postStep = toRun;
        }

        void setResetting(boolean resetting) {
            this.resetting = resetting;
        }

        public void setInterpolator(@Nullable Interpolator interpolator) {
            this.interpolator = interpolator;
        }


        /**
         * Decides if the step is at least minimally defined. Empty steps
         * will simply be a pause - unless it's the final step, then it
         * will be discarded.
         */
        boolean isEmpty() {
            return !hasAnimation() && preStep==null && !resetting;
        }

        boolean hasAnimation() {
            return     rotateByDegrees!=null
                    || rotateToDegrees!=null
                    || translationX!=null
                    || translationY!=null
                    || translationZ!=null
                    || scaleX!=null
                    || scaleY!=null
                    || alpha!=null
                    || resetting;
        }

        void setDurationIfUnset(int ms) {
            if (durationMs <= 0) durationMs = ms;
        }

        void execute() {
            View view = viewRef.get();
            if (view == null){
                Log.i("AndroidAnimationBuilder", "Aborting animation step: View was cleaned up");
                return;
            }

            if (referencingTag!=null && view.getTag() != referencingTag){
                Log.i("AnimationBuilder", "Aborting animation step: View tag has changed!");
                return;
            }

            if (preStep != null){
                preStep.run(view);
            }

            if (hasAnimation()){
                ViewPropertyAnimator animate = view.animate();

                if (resetting){
                    animate.alpha(startState.alpha);

                    animate.scaleX(startState.scaleX);
                    animate.scaleY(startState.scaleY);

                    animate.translationX(startState.translationX);
                    animate.translationY(startState.translationY);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                        animate.translationZ(startState.translationZ);
                    }

                    animate.rotation(startState.rotation);
                }

                if (rotateToDegrees != null){
                    animate.rotation(rotateToDegrees);
                } else if (rotateByDegrees != null){
                    animate.rotationBy(rotateByDegrees);
                }

                if (translationX != null) animate.translationX(translationX);
                if (translationY != null) animate.translationY(translationY);

                if (translationZ != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    animate.translationZ(translationZ);
                }

                if (scaleX != null) animate.scaleX(scaleX);
                if (scaleY != null) animate.scaleY(scaleY);

                if (alpha != null) animate.alpha(alpha);

                if (interpolator != null) animate.setInterpolator(interpolator);
                else animate.setInterpolator(new AccelerateDecelerateInterpolator());

                // add other animation types here.

                animate.setDuration(durationMs);
                animate.setListener(this);
                animate.start();
            } else {
                view.postDelayed(new Runnable() {
                    @Override  public void run() {
                        stepFinished();
                    }
                }, durationMs);
            }
        }

        /**
         * Called when the step was finished.
         */
        private void stepFinished() {
            if (canceled){
                if (DEBUG_LOGGING) {
                    Log.i("AndroidAnimationBuilder", "NOT processing step end: Already canceled!");
                }
                return;
            }

            if (stepAlreadyFinished){
                if (DEBUG_LOGGING) {
                    Log.i("AndroidAnimationBuilder", "NOT re-notifying step end: Already ended!");
                }
                return;
            }
            stepAlreadyFinished = true;

            View view = viewRef.get();
            if (view == null){
                if (DEBUG_LOGGING){
                    Log.i("AndroidAnimationBuilder", "Aborting animation step when scheduling next step: View was cleaned up");
                }
                return;
            }

            if (postStep != null) postStep.run(view);

            view.postDelayed(new Runnable() {
                @Override public void run() {
                    nextStep.execute();
                }
            }, 1);
        }


        @Override  public void onAnimationEnd(Animator animator) {
            stepFinished();
        }

        @Override  public void onAnimationStart(Animator animator) {}
        @Override  public void onAnimationCancel(Animator animator) {
            canceled = true;
            if (DEBUG_LOGGING){
                Log.i("AndroidAnimationBuilder", "Canceled. No further animations will be executed.");
            }
        }
        @Override  public void onAnimationRepeat(Animator animator) {}
    }

    /**
     * Special no-op final step of the animation. Undoes any changes
     * to layer settings done to make the animation smooth.
     */
    private static class FinalStep extends AnimationStep {
        private Integer endLayerType;

        @Override
        public void execute() {
            if (DEBUG_LOGGING){
                Log.d("AndroidAnimationBuilder","Animation done!");
            }

            View view = viewRef.get();
            if (view != null && endLayerType != null){
                view.setLayerType(endLayerType, null);
            }
        }

        public void setEndLayerType(int layerType) {
            this.endLayerType = layerType;
        }
    }

    /**
     * Encapsulates the state of the view at the beginning of the animation
     * for later comparison and undoing.
     */
    private static class StartState {
        public final float alpha;
        public final float scaleX;
        public final float scaleY;
        public final float translationX;
        public final float translationY;
        public final float translationZ;
        public final float rotation;

        public StartState(@Nullable View view) {
            if (view == null){
                alpha = 0;
                scaleX = scaleY = 0;
                translationX = translationY = translationZ = 0;
                rotation = 0;
            } else {
                alpha = view.getAlpha();

                scaleX = view.getScaleX();
                scaleY = view.getScaleY();

                translationX = view.getTranslationX();
                translationY = view.getTranslationY();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                    translationZ = view.getTranslationZ();
                } else {
                    translationZ = 0;
                }

                rotation = view.getRotation();
            }
        }
    }

    /**
     * Objects implementing this interface can be added to any animation step
     * to be called when the animation step is triggered.
     */
    public interface AnimationStepHook {
        /**
         * Run when an animation step is triggered. Only called when the view
         * was not previously cleaned up as the AnimationBuilder does *not* retain
         * the view (-> memory)
         */
        void run(@NonNull View view);
    }

    /**
     * A predefined animation hook (can be attached to animation steps) that
     * removes *all* animation-typical transformations from the view (translation,
     * scale, rotation, ...). <em>This includes alpha</em>, as assigned to the view
     * (in contrast to stuff like transparent backgroundColor colors or images).
     * <br/><br/>
     * <b>NOTE:</b> Unlike a 'reset()' animation step, this will take immediate effect
     * (= not animated) and always deliver the same result. 'reset' will always go
     * back to the state before all animations took place, including all pre-existing
     * transformations.
     */
    public static AnimationStepHook CLEAN = new AnimationStepHook() {
        @Override
        public void run(@NonNull View view) {
            view.setAlpha(1);

            view.setRotation(0);

            view.setScaleX(1);
            view.setScaleY(1);

            view.setTranslationX(0);
            view.setTranslationY(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
                view.setTranslationZ(0);
            }

        }
    };
}