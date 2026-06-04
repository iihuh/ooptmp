package com.hashvis.model.collision;

import java.util.List;

import com.hashvis.model.hashfunc.HashFunction;
import com.hashvis.model.table.Item;
import com.hashvis.model.table.Row;
abstract class OpenAddressing extends ActionProcessor {
  private HashAction action;
  private Integer probeCount = 0;
  private Integer hashValue = null;
  private HashFunction hashFunc;
  private Row currentRow = null;
  private int keyCount=0;
  private Row mark=null;
  private Item item = null;
  private boolean ghost=false;

  abstract protected int handleBucketSelection(int probeCount);

  @Override
  public boolean useSeparateChaining() {return false;}
  @Override
  public void setHashFunctionFields(List<HashFunction> hashFunctions) {hashFunc = hashFunctions.get(0);}
  @Override
  protected void uniqueInitalize(HashAction action){
    this.action  = action;
    probeCount   = 0;
    hashValue    = null;
    currentRow   = null;
    mark         = null;
    ghost        = false;
  }
  @Override
  protected Result firstStep() {
    if(keyCount==table.size() && action == HashAction.INSERT){return new Result("Error: Table is full", -1);}
    if (hashValue == null){return handleHashing();}
    return null;
  }
  private Result handleHashing() {
    hashValue = hashFunc.compute(key, table.size());
    return new Result("Hash value: " + hashValue, 0);
  }
  private boolean searching_bound(Row row) {
    try {
      item = row.getItems().get(0);
    } catch (Exception e) {
      if(mark==null){mark = row;}
      currentRow=null;
      return true;
    }
    if (item.isGhosted()){
      ghost=true;
      if(mark==null){mark = row;}
      return false;
    }
    if (item.getName().equals(key)) {return true;}
    return false;
  }
  @Override 
  protected Result searching(){
    if (currentRow == null){
      if (probeCount == table.size()){probeCount=0;return null;}
      currentRow = table.getRow((hashValue + handleBucketSelection(probeCount)) % table.size());
      probeCount++;
      return new Result("Accessing bucket index " + currentRow.getIndex(), 0);
    }
    if(searching_bound(currentRow)){return processFoundItem(currentRow);}
    currentRow=null;
    return new Result("Checking item: " + item.getName() + " No match", 0);
  }
  private Result processFoundItem(Row row) {
    if(row==null){return null;}
    switch (action){
      case (HashAction.INSERT) -> { return new Result("Error: Duplicate key " + key, -1);}
      case (HashAction.DELETE) -> {
        item = row.getItems().get(0);
        item.ghost();
        keyCount--;
        return new Result("Deleted key " + key, -1);
      }  
      default-> { return new Result("Found key " + key, -1);}
    }  
  }
  @Override
  protected Result processInsertion(){
    if(mark==null){return new Result("Can't insert " + key + " key into the hash table " , -1);}
    if(ghost){mark.removeItem(mark.getItems().get(0));}
    mark.addItem(key);
    keyCount++;
    return new Result("Inserted " + key + " into bucket " + mark.getIndex(), -1);
  }
}