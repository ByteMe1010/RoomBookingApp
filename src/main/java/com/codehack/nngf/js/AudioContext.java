package com.codehack.nngf.js;

public class AudioContext extends Prop {
    public AudioContext(Context ctx) {
        super(ctx);
    }

    public AudioContext(Context ctx, Metd metd) {
        super(ctx, metd);
    }

    public ValInt SampleRate() {
        ctx.AddProperty("sampleRate");
        return new ValInt(ctx);
    }

    public ValInt BaseLatency() {
        ctx.AddProperty("baseLatency");
        return new ValInt(ctx);
    }

    public ValInt OutputLatency() {
        ctx.AddProperty("outputLatency");
        return new ValInt(ctx);
    }

    public Prop Destination() {
        ctx.AddProperty("destination");
        return new Prop(ctx);
    }

    public Prop CurrentTime() {
        ctx.AddProperty("currentTime");
        return new Prop(ctx);
    }

    public ValStr State() {
        ctx.AddProperty("state");
        return new ValStr(ctx);
    }

    public Prop createOscillator() {
        ctx.AddMethod("createOscillator");
        return new Prop(ctx);
    }

    public Prop createGain() {
        ctx.AddMethod("createGain");
        return new Prop(ctx);
    }

    public Prop createBuffer(int channels, int length, int sampleRate) {
        ctx.AddMethod("createBuffer", channels, length, sampleRate);
        return new Prop(ctx);
    }

    public Prop createMediaElementSource(Prop element) {
        ctx.AddMethod("createMediaElementSource", element);
        return new Prop(ctx);
    }

    public Prop createMediaStreamSource(Prop stream) {
        ctx.AddMethod("createMediaStreamSource", stream);
        return new Prop(ctx);
    }

    public Prop createAnalyser() {
        ctx.AddMethod("createAnalyser");
        return new Prop(ctx);
    }

    public Prop createBiquadFilter() {
        ctx.AddMethod("createBiquadFilter");
        return new Prop(ctx);
    }

    public Prop createConvolver() {
        ctx.AddMethod("createConvolver");
        return new Prop(ctx);
    }

    public Prop createDelay(int maxDelayTime) {
        ctx.AddMethod("createDelay", maxDelayTime);
        return new Prop(ctx);
    }

    public Prop createDynamicsCompressor() {
        ctx.AddMethod("createDynamicsCompressor");
        return new Prop(ctx);
    }

    public Prop createPanner() {
        ctx.AddMethod("createPanner");
        return new Prop(ctx);
    }

    public Prop createSpatialPanner() {
        ctx.AddMethod("createSpatialPanner");
        return new Prop(ctx);
    }

    public Prop createStereoPanner() {
        ctx.AddMethod("createStereoPanner");
        return new Prop(ctx);
    }

    public Prop createWaveShaper() {
        ctx.AddMethod("createWaveShaper");
        return new Prop(ctx);
    }

    public Prop createPeriodicWave(Prop real, Prop imag) {
        ctx.AddMethod("createPeriodicWave", real, imag);
        return new Prop(ctx);
    }

    public SEnd resume() {
        ctx.AddMethod("resume");
        return new SEnd(ctx);
    }

    public SEnd suspend() {
        ctx.AddMethod("suspend");
        return new SEnd(ctx);
    }

    public SEnd close() {
        ctx.AddMethod("close");
        return new SEnd(ctx);
    }
}
