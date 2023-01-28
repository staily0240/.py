// For reading images from gallery and drawing the output in an ImageView.
HandsOptions handsOptions =
    HandsOptions.builder()
        .setStaticImageMode(true)
        .setMaxNumHands(2)
        .setRunOnGpu(true).build();
Hands hands = new Hands(this, handsOptions);

// Connects MediaPipe Hands Solution to the user-defined ImageView instance that
// allows users to have the custom drawing of the output landmarks on it.
// See mediapipe/examples/android/solutions/hands/src/main/java/com/google/mediapipe/examples/hands/HandsResultImageView.java
// as an example.
HandsResultImageView imageView = new HandsResultImageView(this);
hands.setResultListener(
    handsResult -> {
      if (result.multiHandLandmarks().isEmpty()) {
        return;
      }
      int width = handsResult.inputBitmap().getWidth();
      int height = handsResult.inputBitmap().getHeight();
      NormalizedLandmark wristLandmark =
          handsResult.multiHandLandmarks().get(0).getLandmarkList().get(HandLandmark.WRIST);
      Log.i(
          TAG,
          String.format(
              "MediaPipe Hand wrist coordinates (pixel values): x=%f, y=%f",
              wristLandmark.getX() * width, wristLandmark.getY() * height));
      // Request canvas drawing.
      imageView.setHandsResult(handsResult);
      runOnUiThread(() -> imageView.update());
    });
hands.setErrorListener(
    (message, e) -> Log.e(TAG, "MediaPipe Hands error:" + message));

// ActivityResultLauncher to get an image from the gallery as Bitmap.
ActivityResultLauncher<Intent> imageGetter =
    registerForActivityResult(
        new ActivityResultContracts.StartActivityForResult(),
        result -> {
          Intent resultIntent = result.getData();
          if (resultIntent != null && result.getResultCode() == RESULT_OK) {
            Bitmap bitmap = null;
            try {
              bitmap =
                  MediaStore.Images.Media.getBitmap(
                      this.getContentResolver(), resultIntent.getData());
              // Please also rotate the Bitmap based on its orientation.
            } catch (IOException e) {
              Log.e(TAG, "Bitmap reading error:" + e);
            }
            if (bitmap != null) {
              hands.send(bitmap);
            }
          }
        });
Intent pickImageIntent = new Intent(Intent.ACTION_PICK);
pickImageIntent.setDataAndType(MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
imageGetter.launch(pickImageIntent);
