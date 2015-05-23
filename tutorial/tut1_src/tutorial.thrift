namespace java tutorial

struct Item {
  1: i32 key
  2: string value
}

service Myservice {

   i32 add(1:i32 num1, 2:i32 num2),

   Item getItem(1: i32 key),
   void putItem(1: Item item)
}

