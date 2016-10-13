# DownloadImage/TextTask

Creates a async task so UI doesnt halt while loading and calls OnTaskComplete when loading is ready.

## Code examples

### DownloadTextTask
```
final TextView textView = (TextView)this.findViewById(id);
OnTaskCompleted task = new OnTaskCompleted() {
    @Override
    public void onTaskCompleted(String result) {
        textView.setText(result)
    }

    @Override
    public void onTaskCompleted(Bitmap result) {

    }
};
new DownloadTextTask(task).execute(url)
```

### DownloadImageTask
```
final ImageViev imageView = (ImageView)this.findViewById(id);
OnTaskCompleted task = new OnTaskCompleted() {
    @Override
    public void onTaskCompleted(String result) {
        
    }

    @Override
    public void onTaskCompleted(Bitmap result) {
        imageView.setImageBitmap(result)
    }
};
new DownloadImageTask(task).execute(url)
```
