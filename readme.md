# Chunks Parallel Downloader

## What else could be improved

### More complex chunk system

Now all chunks are the same size and there is only one download order.
This could be improved by custom chunks generator.

#### Custom chunks generator

Function could accept custom chunks generator, which allow to change an order and chunks properties.
In this case, for example, we would be able to load firstly some small chunk at the beginning of a file,
then in the end, and then all others.

### Better exceptions handling

Currently, if chunk load fails, then file load fails.
This could be improved in a few ways.

#### Fails — try again

The easiest logic would be to repeat attempt to load a chunk right after load of the chunk fails.
More complex strategies could also be created. For example, we could try to download failed
chunks at the end of download, or at the moment when some amount of chunks failed.
This could be implemented as a channel of failed chunks, and some logic that decides, 
should it use an iterator, or the channel to get next chunk for processing.

The general solution could look like an atomic queue with possibility to insert a failed chunk at any position.

#### Partial result

The result of a load should not be a completed work. We could return a structure that describes what 
chunks were downloaded and what were not. 
Then there could be an oppotunity to save this structure and to continue a download.