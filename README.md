# UtilsJ

UtilsJ is a library of collections, synchronisation and other utility classes that have been created to help with performance and scalability requirements of applications. The library is a dependency of the [QueuJ](https://github.com/workplacesystems/queuj/) job queue library.

## Features

 * **Transactional Collections** - Changes made to a collection by one thread will not be visible to any other thread until the changes are committed. Changes can also be undone with a rollback and multiple threads can modify collections at the same time (although individual methods still need to be syncrhonised).

 * **In-place Filtered Collections** - A Filter can be provided to the collection to get a virtual filtered collection or view of the original collection backed by the original storage. The filter is only called when the iteration of the virtual collection is done and synchronisation of the virtual collection will synchronise the original storage. Filtering using standard methods requires the iteration of the collection and creation of a new collection. The new collection uses extra memory and maybe stale before final use.

 * **Iteration Inversion** - IterativeCallback provides inversion of iteration so that read syncrhonisation can be done automatically. It also allows for better tier seperation. For instance Collections of business objects don't have to be passed into the presentation layer, instead the presentation IterativeCallback can be passed into the business layer.

 * **Read/Write Syncrhonisation** - Synchronisation utilities to implement common read/write synchronisation patterns using ReentrantReadWriteLock including read/write synchronisation decorators.

 * **Thread Pool** - Thread pool implementation using Apache commons-pool.

## Collections

TransactionalBidiTreeMap is the main collection class of the UtilsJ library. It's a Red-Black tree based implementation of Map that is transactional; has in-place filtering and is double ordered (key and value). It was forked from Apache commons-collections DoubleOrderedMap (now TreeBidiMap) at revision 1.6.

FilterableArrayList and FilterableTreeSet are ArrayList and TreeSet overrides respectively that provide in-place filtering and can be used as drop in replacements of their super classes.

TransactionalHashMap is a transactional HashMap implementation that can be used as a drop in replacement for HashMap.

## Synchronisation

SyncUtils uses ReentrantReadWriteLock to provide various common syncrhonisation read/write patterns that are difficult to get right when implemented manually. The synchronisation patterns provided are read; write; write then downgrade to read; conditional write then take or downgrade to read; and synchronisation of a list of Objects to avoid StackOverflowException.

Callback is an abstract class used for providing the code to be synchronised in SyncUtils.

Condition is an interface for providing the condition implementation for conditional synchronisation in SyncUtils.

## Decorators

Synchronised decorators for all standard Java and UtilsJ collections. The decorators use SyncUtils to syncrhonise for read or write as required.

## Collection Utilities

Filter is an interface for implementing the filter on collections that support filtering.

IterativeCallback is an abstract class that automates synchronous iteration of a collection.

## Helpers

IsEmptyIterativeCallback and NotEmptyIterativeCallback are IterativeCallback implementations that provide isEmpty and !isEmpty functionality on a collection. Default Java collections isEmpty methods are implemented with c.size() == 0 which iterate all elements in the collection. These classes use c.iterator().hasNext() which is much more efficient.

HasLessThan is an IterativeCallback implementation that implements the 'size() < X' check by only iterating up to X number of elements.

AndFilter, OrFilter and NotFilter are filter implementations that provide and, or and not operators respectively to 2 or more other filters.

## Usage

To make the map transactional you must call setAutoCommit(false) on the newly created map.

```java
TransactionalBidiTreeMap<String,Integer> map = new TransactionalBidiTreeMap<String, Integer>();
map.setAutoCommit(false);
try {
    map.put("one", 1);
    map.put("two", 2);
    map.commit();
}
catch (Exception e) {
    map.rollback();
    throw new UtilsjException(e);
}
```

To synchronise the map select the appropriate decorator and call its decorate method.

```java
TransactionalSortedFilterableBidiMap<String,Integer> map =
        SynchronizedTransactionalSortedFilterableBidiMap.decorate(new TransactionalBidiTreeMap<String, Integer>());
```

To filter the map to contain only values as per a Filter.

```java
FilterableBidiMap<String, Integer> filteredMap = map.filteredMapByValue(new Filter<Integer>() {
    @Override
    public boolean isValid(Integer value) {
        return value >= 2;
    }
});
```

To iterate the values of the filtered map in the order of the values. This will automatically read synchronise the original map thereby blocking write methods but allowing other iterations and get methods.

```java
(new IterativeCallback<Integer, Void>() {
    @Override
    protected void nextObject(Integer value) {
        System.out.println("Iterated value " + value);
    }
}).iterate(filteredMap.valuesByValue());
```

To write synchronise the iteration so you can modify the map during the iteration use SyncUtils.synchronizeWrite.

```java
try {
    SyncUtils.synchronizeWrite(filteredMap, new Callback<Void>() {
        @Override
        protected void doAction()
        {
            (new IterativeCallback<Integer, Void>() {
                @Override
                protected void nextObject(Integer value) {
                    remove();
                }
            }).iterate(filteredMap.valuesByValue());
        }
    });
    map.commit();
}
catch (Exception e) {
    map.rollback();
    throw new UtilsjException(e);
}
```
