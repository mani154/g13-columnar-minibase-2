## Command for test3 in columnarFileTests
### Steps
- cd ./src/tests 
- make columnarTest

## Some queries
- batchInsert datafile.txt testdb test4 4
- query testdb test4 [A,B,D] {C >= 7} 11 FILESCAN
- index testdb test4 A BITMAP
- index testdb test4 A BTREE

- query testdb test4 [A,C,D] {A >= South_Dakota} 11 BTREE
- query testdb test4 [A,B,D] {A >= South_Dakota} 11 COLUMNSCAN
- query testdb test4 [A,C,D] {A >= Arizona} 11 BITMAP
- query testdb test4 [A,C,D] {A >= South_Dakota} 11 BTREE
- query testdb test4 [A,B,D] {B = Kansas} 11 FILESCAN
- query testdb test4 [A,C,D] {A >= Arizona} 11 BTREE

- query testdb test4 [A,C,D] {A <= South_Dakota} 11 BTREE
- query testdb test4 [A,B,D] {A <= South_Dakota} 11 COLUMNSCAN
- query testdb test4 [A,C,D] {A <= Arizona} 11 BITMAP
- query testdb test4 [A,C,D] {A <= South_Dakota} 11 BTREE
- query testdb test4 [A,B,D] {B = Kansas} 11 FILESCAN
- query testdb test4 [A,C,D] {A <= Arizona} 11 BTREE

- query testdb test4 [A,C,D] {A < South_Dakota} 11 BTREE
- query testdb test4 [A,B,D] {A < South_Dakota} 11 COLUMNSCAN
- query testdb test4 [A,C,D] {A < Arizona} 11 BITMAP
- query testdb test4 [A,C,D] {A < South_Dakota} 11 BTREE
- query testdb test4 [A,B,D] {B > Kansas} 11 FILESCAN
- query testdb test4 [A,C,D] {A < Arizona} 11 BTREE

- query testdb test4 [A,C,D] {A > South_Dakota} 11 BTREE
- query testdb test4 [A,B,D] {A > South_Dakota} 11 COLUMNSCAN
- query testdb test4 [A,C,D] {A = Arizona} 11 BITMAP
- query testdb test4 [A,C,D] {A = South_Dakota} 11 BTREE
- query testdb test4 [A,B,D] {B >= Kansas} 11 FILESCAN
- query testdb test4 [A,C,D] {A = Arizona} 11 BTREE

- delete_query testdb test4 {C >= 7} 11 True


### Queries for command based access ( not working, cannot fetch existing database )

- batchInsert ../datafile.txt testdb test4 4
- query testdb test4 \[A,B,D\] \{C >= 7\} 11 FILESCAN
- index testdb test4 A BITMAP
- query testdb test4 \[A,C,D\] \{A >= South_Dakota\} 11 BTREE
- delete_query testdb test4 \{C >= 7\} 11 True
