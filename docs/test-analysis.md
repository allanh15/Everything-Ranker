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

---

## USE CASE 3: NAVIGATE LIST (browse / search public lists)

Methods under test in `ListManager`:
- `browsePublicLists(): List<EverythingList>`
- `searchLists(String titleQuery, String authorUsername): List<EverythingList>`

### Figure 20.13 Style — Identifying Input Values

| Input Element   | Type   | Value Specification                                    | Valid                              | Invalid                          | Exceptional Cases                                |
|-----------------|--------|--------------------------------------------------------|------------------------------------|----------------------------------|--------------------------------------------------|
| titleQuery      | String | Partial title; case-insensitive substring match        | Non-empty string                   | N/A                              | null, empty, whitespace-only (= no title filter) |
| authorUsername  | String | Exact username of an existing user                     | Existing username (case-sensitive) | Username that does not exist     | null, empty, whitespace-only (= no author filter)|

(`browsePublicLists()` takes no input — it returns every list with `access = true`.)

### Figure 20.14 Style — Test Case Generation

**browsePublicLists()**

| Test Case | DB State                                | Expected Outcome                                   |
|-----------|-----------------------------------------|----------------------------------------------------|
| 1         | Empty DB                                | Empty list (non-null)                              |
| 2         | Only private lists                      | Empty list                                         |
| 3         | Mix of public + private                 | Only public lists returned                         |
| 4         | Mix of public + private                 | Every returned list has `access == true`           |
| 5         | Mix of public + private                 | Private lists never appear in results              |
| 6         | Public lists from multiple authors      | All public lists returned regardless of author     |

**searchLists(titleQuery, authorUsername)**

| Test Case | titleQuery               | authorUsername | Expected Outcome                                |
|-----------|--------------------------|----------------|-------------------------------------------------|
| 7         | null                     | null           | All public lists                                |
| 8         | ""                       | ""             | All public lists                                |
| 9         | whitespace               | whitespace     | All public lists                                |
| 10        | Valid (matches >1)       | null           | All public lists matching the title             |
| 11        | Valid (different case)   | null           | Same matches (case-insensitive)                 |
| 12        | Valid (matches 1)        | null           | Single match                                    |
| 13        | Valid (no matches)       | null           | Empty list                                      |
| 14        | Valid (matches private)  | null           | Empty list (private excluded)                   |
| 15        | null                     | Existing       | That author's PUBLIC lists                      |
| 16        | null                     | Existing       | Single match when author has 1 public list      |
| 17        | null                     | Non-existent   | Empty list                                      |
| 18        | Valid                    | Existing       | Intersection (title AND author match)           |
| 19        | Valid                    | Existing       | Empty when title belongs to a different author  |
| 20        | Valid                    | Existing       | Single match when both filters narrow to one    |
| 21        | Valid (private title)    | Existing       | Empty list (private excluded)                   |
| 22        | Valid                    | null           | Empty list (and non-null) on empty DB           |
| 23        | Valid w/ extra whitespace| null           | Trimmed and matched (same as TC10)              |
| 24        | Valid                    | Existing       | Returned list has correct ID/title/author/access|

### Figure 20.15 Style — Concrete Test Values

**Seed data used for most cases (`seedFiveLists`):**

| List Title    | Author (userID)   | Access  |
|---------------|-------------------|---------|
| "Best Foods"  | alice (00000001)  | public  |
| "Worst Foods" | alice (00000001)  | public  |
| "Top Movies"  | alice (00000001)  | public  |
| "Secret List" | alice (00000001)  | private |
| "My Picks"    | bob   (00000002)  | public  |

| Test Case | Input                                  | Expected Result                                                       |
|-----------|----------------------------------------|-----------------------------------------------------------------------|
| 1         | browsePublicLists() on empty DB        | size = 0, non-null                                                    |
| 2         | browsePublicLists() with 2 private     | size = 0                                                              |
| 3         | browsePublicLists() on seed            | size = 4                                                              |
| 4         | browsePublicLists() on seed            | every list has access = true                                          |
| 5         | browsePublicLists() on seed            | "Secret List" not present                                             |
| 6         | browsePublicLists() on seed            | contains "Best Foods" AND "My Picks"                                  |
| 7         | searchLists(null, null)                | size = 4 (no "Secret List")                                           |
| 8         | searchLists("", "")                    | size = 4                                                              |
| 9         | searchLists("   ", "   ")              | size = 4                                                              |
| 10        | searchLists("Foods", null)             | size = 2 → {"Best Foods", "Worst Foods"}                              |
| 11        | searchLists("foods", null)             | size = 2 (case-insensitive)                                           |
| 12        | searchLists("Movies", null)            | size = 1 → {"Top Movies"}                                             |
| 13        | searchLists("xyz_no_match", null)      | size = 0                                                              |
| 14        | searchLists("Secret", null)            | size = 0 (private excluded)                                           |
| 15        | searchLists(null, "alice")             | size = 3 (no "Secret List")                                           |
| 16        | searchLists(null, "bob")               | size = 1 → {"My Picks"}                                               |
| 17        | searchLists(null, "ghost_user")        | size = 0                                                              |
| 18        | searchLists("Foods", "alice")          | size = 2                                                              |
| 19        | searchLists("Foods", "bob")            | size = 0                                                              |
| 20        | searchLists("Picks", "bob")            | size = 1 → {"My Picks"}                                               |
| 21        | searchLists("Secret", "alice")         | size = 0                                                              |
| 22        | searchLists("anything", null) empty DB | size = 0, non-null                                                    |
| 23        | searchLists("  Foods  ", null)         | size = 2                                                              |
| 24        | searchLists("Best", "alice")           | size = 1, returned list has id > 0, title="Best Foods", access=true   |
| 25        | searchLists("Foods", "ghost_user")     | size = 0 (non-existent author wins over title match)                  |
| 26        | searchLists("Foods", "   ")            | size = 2 (whitespace author = no-author filter, title-only)           |
| 27        | searchLists("   ", "alice")            | size = 3 (whitespace title = no-title filter, author-only)            |
| 28        | searchLists("Foods", "")               | size = 2 (empty author = no-author filter)                            |
| 29        | author "carol" exists, only private    | searchLists(null, "carol") → size = 0                                 |
| 30        | author "dave" exists, zero lists       | searchLists(null, "dave") → size = 0                                  |
| 31        | "Shared Title" exists for alice & bob  | title-only → 2; title+alice → 1 (alice); title+bob → 1 (bob)          |
| 32        | list with 2 items                      | browse and search both return list with 2 items preserved             |

### Implementation Notes

- All concrete test cases live in `NavigateListTest.java` and use `assertEquals` / `assertTrue` (no JUnit dependency, matching the pattern used by the other test classes in this repo).
- A schema fix was required while writing these tests: `USERS.ID` was declared as `INT PRIMARY KEY`, which caused SQLite's INTEGER type affinity to drop leading zeros from 8-digit user IDs (e.g., `"00000001"` was read back as `"1"`). The user ID is contractually a "8-digit string of numbers" (see `User.java`). The column was changed to `TEXT PRIMARY KEY` so `searchLists(_, username)` can correctly look up the author and match against `LISTS.AUTHOR_ID`.
