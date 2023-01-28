// For camera input and result rendering with OpenGL.
HandsOptions handsOptions =
    HandsOptions.builder()
        .setStaticImageMode(false)
        .setMaxNumHands(2)
        .setRunOnGpu(true).build();
Hands hands = new Hands(this, handsOptions);
hands.setErrorListener(
    (message, e) -> Log.e(TAG, "MediaPipe Hands error:" + message));

// Initializes a new CameraInput instance and connects it to MediaPipe Hands Solution.
CameraInput cameraInput = new CameraInput(this);
cameraInput.setNewFrameListener(
    textureFrame -> hands.send(textureFrame));

// Initializes a new GlSurfaceView with a ResultGlRenderer<HandsResult> instance
// that provides the interfaces to run user-defined OpenGL rendering code.
// See mediapipe/examples/android/solutions/hands/src/main/java/com/google/mediapipe/examples/hands/HandsResultGlRenderer.java
// as an example.
SolutionGlSurfaceView<HandsResult> glSurfaceView =
    new SolutionGlSurfaceView<>(
        this, hands.getGlContext(), hands.getGlMajorVersion());
glSurfaceView.setSolutionResultRenderer(new HandsResultGlRenderer());
glSurfaceView.setRenderInputImage(true);

hands.setResultListener(
    handsResult -> {
      if (result.multiHandLandmarks().isEmpty()) {
        return;
      }
      NormalizedLandmark wristLandmark =
          handsResult.multiHandLandmarks().get(0).getLandmarkList().get(HandLandmark.WRIST);
      Log.i(
          TAG,
          String.format(
              "MediaPipe Hand wrist normalized coordinates (value range: [0, 1]): x=%f, y=%f",
              wristLandmark.getX(), wristLandmark.getY()));
      // Request GL rendering.
      glSurfaceView.setRenderData(handsResult);
      glSurfaceView.requestRender();
    });

// The runnable to start camera after the GLSurfaceView is attached.
glSurfaceView.post(
    () ->
        cameraInput.start(
            this,
            hands.getGlContext(),
            CameraInput.CameraFacing.FRONT,
            glSurfaceView.getWidth(),
            glSurfaceView.getHeight()));
