# Chunks Parallel Downloader

## Run tests

To run tests on Linux use: 

```shell
docker-compose up -d
./gradlew test
```

## What else could be improved

### More Flexible Chunking Strategy

Currently, all chunks are the same size and downloaded in a fixed sequential order.
This could be improved by introducing a custom chunk generator.

#### Custom chunks generator

The function could accept a custom chunk generator, which allows changing the order and properties of chunks.
For example, this would enable downloading a small chunk from the beginning of a file first, 
then one from the end, and finally the remaining chunks in between.

### Better exceptions handling

Currently, if a single chunk fails to load, the entire file download fails.
This could be improved in several ways.

#### Retry on Failure

The simplest approach would be to retry loading a chunk immediately after it fails.
More sophisticated strategies could also be implemented. 
For example, failed chunks could be retried at the end of the download process, 
or once a certain number of chunks have failed.
This could be implemented using a channel for failed chunks, 
combined with logic that decides whether to fetch the next chunk from the main iterator or from the retry channel.

A more general solution could use an atomic queue 
that allows inserting a failed chunk at any position for later reprocessing.

#### Partial result

The result of a download should not be an all-or-nothing outcome. 
Instead, we could return a structure that describes which chunks were successfully downloaded and which were not.
This would provide an opportunity to save this state and resume the download later, 
rather than starting over from the beginning.