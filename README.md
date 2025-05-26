
---
## introduction
1. User registration and login
Supports new user registration, and needs to fill in name, Deakin email address, password, confirm password and major.
Login only requires Deakin email address and password, and supports login status persistence.
The personal information interface can view and manage information such as name, email address, major, etc.
Supports one-click secure logout.
2. Personalized learning plan generation
Users enter the course name, learning goals and start and end dates.
The backend automatically generates a detailed daily learning plan (JSON format) through Llama 2, including daily tasks, learning suggestions and recommended resources.
The plan content is automatically mapped to the calendar date for easy tracking.
Supports saving, viewing and managing multiple historical learning plans.
3. Learning plan visualization
Use MPAndroidChart to dynamically generate pie charts to intuitively display the distribution of learning time for each course/task.
The pie chart data is dynamically generated according to the actual learning plan content to help users allocate learning time reasonably.
4. AI intelligent question and answer (AI Q&A)
Users can enter any learning-related questions.
The backend calls Llama 2 and returns intelligent, contextual English answers.
Supports both context-free and context-based questioning.
5. Intelligent summary of notes
Users can paste or upload study notes.
The backend generates concise English summaries through Llama 2 to help review efficiently.
6. History and details view
The history page displays all saved study plans, sorted by time.
Click on the history plan to view detailed content, including daily tasks, learning goals, course names, AI suggestions, etc.
7. Personal information management
The personal center page displays the current user's name, email address, and major.
Supports logging out at any time to protect user privacy.

---
## Development Process

- **Agile Iteration:** Weekly meetings with tutor, regular feedback and feature adjustments.
- **UI/UX Design:** Material Design principles, English localization, responsive layouts.
- **Backend Integration:** RESTful API, prompt engineering for Llama 2, error handling.
- **Data Persistence:** Room for study plans, SharedPreferences for user info and session.
- **Testing:** Multi-device testing, bug fixing, and performance optimization.

### Key Challenges & Solutions

- **Llama 2 Integration:**  
  - Challenge: Slow response and timeouts.  
  - Solution: Increased timeout, prompt optimization, error handling.

- **API Consistency:**  
  - Challenge: Parameter and data structure mismatches.  
  - Solution: Unified API documentation, regular front-backend sync.

- **UI Adaptation:**  
  - Challenge: Layout issues on different devices.  
  - Solution: Used ConstraintLayout, ScrollView, and extensive testing.

- **Data Storage:**  
  - Challenge: SharedPreferences not suitable for complex data.  
  - Solution: Migrated to Room database for study plans.

- **Session Management:**  
  - Challenge: Ensuring secure and persistent login state.  
  - Solution: Used SharedPreferences, added logout button.

---

## FAQ

**Q: Why use Llama 2?**  
A: Llama 2 provides strong natural language understanding and generation, enabling personalized, context-aware study support.

**Q: Can I use this app without Deakin email?**  
A: Registration requires a Deakin email for authentication.

**Q: Is my data secure?**  
A: All user data is stored locally on the device. No sensitive data is sent to external servers.

---

## Future Work

- Integrate cloud-based AI for faster, more scalable responses.
- Add features: study reminders, progress tracking, social sharing.
- Enhance security: encrypted storage, OAuth login.
- Multi-language support and accessibility improvements.
- Release iOS and web versions.

---

## License

This project is for educational use at Deakin University.

---

## Author
Shicheng Xiang
