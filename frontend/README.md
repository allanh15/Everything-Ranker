# EverythingRanker — Frontend Wireframes

Static HTML wireframes for all pages of the EverythingRanker web app. All pages are fully linked to one another and ready for backend integration.

---

## Pages

| File | Page |
|---|---|
| `homepage.html` | Home page — displays most recent/popular ranked lists in a searchable table |
| `login.html` | Login & Create Account page — two forms, login and sign up |
| `ranking.html` | Ranking page — drag-and-drop interface for ranking list items |
| `edit_list.html` | Edit List page — create or edit a list of items to be ranked |
| `view_your_ranked_list.html` | View Your Ranked List — shows the logged-in user's completed ranking with medals |
| `view_users_ranked_list.html` | View User's Ranked List — shows another user's completed ranking (read-only) |
| `view_other_rankings.html` | View Other Rankings — shows all other users' rankings for a given list |
| `view_your_profile.html` | View Your Profile — displays user's info, their lists, and their rankings |
| `settings.html` | Settings — account settings for the logged-in user |

---

## Navigation Flow

```
Homepage
├── Login / Sign Up → login.html
├── Popular → view_other_rankings.html
├── Table row (username) → view_your_profile.html
├── Table row (category) → view_your_ranked_list.html
├── See more → view_other_rankings.html
└── Create New List → edit_list.html

Login
└── Submit (login or create account) → homepage.html

Edit List
└── Done → ranking.html

Ranking
└── (completes to) → view_your_ranked_list.html

View Your Ranked List
├── View Other Rankings → view_other_rankings.html
└── Re-rank → ranking.html

View Other Rankings
├── Username → view_your_profile.html
└── Start New Ranking → ranking.html

View User's Ranked List
├── Back → view_other_rankings.html
├── View Other Rankings → view_other_rankings.html
├── View Profile → view_your_profile.html
└── Create your own ranking → edit_list.html

View Your Profile
├── Settings → settings.html
├── My Lists → edit_list.html
└── My Ranks → view_your_ranked_list.html

Settings
└── Back to Profile → view_your_profile.html
```

---

## Backend Integration Notes

### CSS
All styles are embedded directly inside each HTML file via `<style>` tags. There is no external stylesheet. The only external dependency is Google Fonts (Inter), loaded via CDN — requires an internet connection.

### Dynamic Data Targets

**homepage.html**
- `#search-input` — search bar, filter table rows dynamically
- `.table-body` — inject list rows from the database (username, category, time)

**login.html**
- `form` inside `.card.login` — `name="username"`, `name="password"` — POST to `/login`
- `form` inside `.card.create-account` — `name="signup_email"`, `name="signup_username"`, `name="signup_password"` — POST to `/register`

**ranking.html**
- `.ranking-list` — populated with items from the selected list
- Items are drag-and-drop sortable; final order should be submitted to the backend

**edit_list.html**
- `.items-list` — list items to be created/edited by the user
- `+ Add New Item` button appends new editable fields
- Done button submits the list

**view_your_ranked_list.html / view_users_ranked_list.html**
- `.ranked-list` — inject ranked items from database; top 3 get gold/silver/bronze medal styling
- `.title` — replace with actual list name
- `.subtitle` — replace with actual category name

**view_other_rankings.html**
- `.table-body` — inject rows of other users' rankings (username, time)

**view_your_profile.html**
- `.username` — replace with logged-in user's username
- `.description` — replace with user's bio/description
- `#lists` — inject user's list links dynamically
- `#ranks` — inject user's ranking links dynamically

**settings.html**
- Form fields — bind to logged-in user's account data for update/save

---

## Notes
- All placeholder text (user_0001, list_01, [item name], etc.) should be replaced with dynamic data from the backend
- All icons are inline SVGs — no external asset files needed
- Pages are linked using relative paths and will work as-is when served from the same directory
