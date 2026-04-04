# Everything Ranker - Test Case Analysis (Arnav)
## Based on Kung Book Figures 20.13, 20.14, 20.15

---

## USE CASE 1: CREATE LIST (UC19 / R19)

### Figure 20.13 Style — Identifying Input Values

| Input Element | Type    | Value Specification                          | Valid                                    | Invalid                                   | Exceptional Cases                          |
|---------------|---------|----------------------------------------------|------------------------------------------|-------------------------------------------|--------------------------------------------|
| Title         | String  | Non-empty string for the list name           | Non-empty, non-whitespace string         | N/A                                       | Empty string, null, whitespace-only        |
| Author ID     | String  | Non-empty string; must identify the creator  | Valid userID string                      | N/A                                       | Empty string, null, whitespace-only        |
| Access        | boolean | true = public, false = private               | true or false                            | N/A                                       | N/A (boolean has no exceptional case)      |

### Figure 20.14 Style — Test Case Generation

| Test Case | Title       | Author ID   | Access | Expected Outcome                   |
|-----------|-------------|-------------|--------|------------------------------------|
| 1         | Valid       | Valid       | false  | List created (returns positive ID) |
| 2         | Valid       | Valid       | true   | List created (returns positive ID) |
| 3         | Exceptional | Valid       | false  | Creation failed (returns -1)       |
| 4         | Valid       | Exceptional | false  | Creation failed (returns -1)       |
| 5         | Exceptional | Exceptional | false  | Creation failed (returns -1)       |

### Figure 20.15 Style — Concrete Test Values

| Test Case | Title          | Author ID  | Access | Expected Result |
|-----------|----------------|------------|--------|-----------------|
| 1         | "Best Foods"   | "00000001" | false  | > 0 (new ID)    |
| 2         | "Top Movies"   | "00000001" | true   | > 0 (new ID)    |
| 3         | ""             | "00000001" | false  | -1              |
| 4         | "Best Foods"   | ""         | false  | -1              |
| 5         | null           | null       | false  | -1              |

### Additional Test Cases

| Test Case | Description                                  | Expected Result                       |
|-----------|----------------------------------------------|---------------------------------------|
| 1b-1g     | Verify DB stores title, author, access, pubDate, update | All attributes persisted correctly |
| 6         | Multiple lists → unique IDs                  | Different IDs assigned                |
| 7         | List author matches creator (R19)            | getAuthor() == authorID               |
| 8         | Non-author cannot delete (R22)               | deleteList returns false              |
| 9         | Author can delete own list                   | deleteList returns true, list gone    |

---

## USE CASE 2: RANK LIST (UC7)

### Figure 20.13 Style — Identifying Input Values

| Input Element | Type         | Value Specification                                          | Valid                                            | Invalid                                              | Exceptional Cases                              |
|---------------|--------------|--------------------------------------------------------------|--------------------------------------------------|------------------------------------------------------|------------------------------------------------|
| List ID       | int          | Must be a valid existing list ID in the database             | List ID exists and list has items                | List ID does not exist in the database               | Negative number, 0                             |
| Author ID     | String       | Non-empty string identifying the user submitting the ranking | Valid userID string                              | N/A                                                  | Empty string, null                             |
| Ranked Items  | List<String> | Ordered list of item names that exist in the target list     | All items exist in list, no duplicates           | Contains item not in list, or contains duplicates    | null, empty list, list with null/empty entries |

### Figure 20.14 Style — Test Case Generation

| Test Case | List ID     | Author ID   | Ranked Items | Expected Outcome                     |
|-----------|-------------|-------------|--------------|--------------------------------------|
| 1         | Valid       | Valid       | Valid        | "ranking successful"                 |
| 2         | Invalid     | Valid       | Valid        | "list not found"                     |
| 3         | Valid       | Exceptional | Valid        | "invalid author"                     |
| 4         | Valid       | Valid       | Invalid (item not in list) | "item not in list"       |
| 5         | Valid       | Valid       | Invalid (duplicates)       | "duplicate item in ranking" |
| 6         | Valid       | Valid       | Exceptional (empty)        | "invalid ranking"        |
| 7         | Valid       | Valid       | Exceptional (null)         | "invalid ranking"        |

### Figure 20.15 Style — Concrete Test Values

| Test Case | List ID | Author ID  | Ranked Items                  | Pre-condition                             | Expected Result            |
|-----------|---------|------------|-------------------------------|-------------------------------------------|----------------------------|
| 1         | 1       | "00000001" | ["Pizza", "Sushi", "Tacos"]   | List 1 exists with Pizza, Sushi, Tacos    | "ranking successful"       |
| 2         | 999     | "00000001" | ["Pizza"]                     | List 999 does not exist                   | "list not found"           |
| 3         | 1       | ""         | ["Pizza", "Sushi", "Tacos"]   | List 1 exists with items                  | "invalid author"           |
| 4         | 1       | "00000001" | ["Pizza", "Sushi", "Burger"]  | List 1 exists but has no "Burger"         | "item not in list"         |
| 5         | 1       | "00000001" | ["Pizza", "Sushi", "Pizza"]   | List 1 exists with items                  | "duplicate item in ranking"|
| 6         | 1       | "00000001" | []                            | List 1 exists with items                  | "invalid ranking"          |
| 7         | 1       | "00000001" | null                          | List 1 exists with items                  | "invalid ranking"          |

### UC7 Sequence Diagram Coverage

| Step | DCD Method                                     | Test Cases |
|------|-------------------------------------------------|------------|
| 1    | AccountManager.validateRankAccess(listID)       | TC11-TC15  |
| 2    | Ranker.startRankingSession(listID)              | TC16, TC20 |
| 3    | DBManager.retrieveListItems(listID)             | TC25       |
| 4    | ERGUI.displayNextPair(item1, item2)             | TC17, TC21 |
| 5    | Ranker.recordChoice(listID, selectedItemID)     | TC18       |
| 6    | DBManager.updateRankedList(listID, rankedData)  | TC19, TC26 |
| 7    | ERGUI.presentFinalResults(listID)               | TC22       |

### Additional Test Cases

| Test Case | Description                                          | Expected Result                         |
|-----------|------------------------------------------------------|-----------------------------------------|
| 8         | Re-ranking overwrites previous                       | New order replaces old in DB            |
| 9         | Partial ranking (subset of items)                    | "ranking successful"                    |
| 10        | List with no items                                   | "list has no items"                     |
| 11-15     | AccountManager access checks (author, non-author, public, null, missing list) | Correct access decisions |
| 16-20     | Ranker pairwise session (start, pairs, choices, complete, bad list) | Session lifecycle works |
| 21-24     | ERGUI display tests (pair, results, confirm, error)  | UI state captured correctly             |
| 25-26     | DBManager retrieveListItems and updateRankedList     | Data round-trips correctly              |
