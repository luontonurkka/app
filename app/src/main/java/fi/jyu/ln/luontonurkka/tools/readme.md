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

# Parsers

Parses CSV files.

## Code examples

### GridParser
```
GridParser p = new GridParser();
p.openFile(new File("example.csv");
HashMap<String, String> grid = p.parseFile();
p.closeFile();

// To search a square based on coordinate, returns a string with
// species separated by comma
String speciesInSquare = grid.get("690:343");
```

### SpeciesParser
```
SpeciesParser p = new SpeciesParser();
p.openFile(new File("example.csv");
HashMap<String, Species> species = p.parseFile();
p.closeFile();

// To search a species based on name, returns a Species object
Species teeri = species.get("Tetrao Tetrix");
```
