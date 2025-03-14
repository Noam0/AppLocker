<h1>AppLockPro: Advanced App Lock and Security Manager 🔒</h1>
<img src="https://github.com/user-attachments/assets/48131f19-2099-46d5-9a6d-614c2bbaf5b8" alt="icon" width="50" height="50">

<hr>

 
<h2>Overview</h2>
<p>AppLockPro is an Android security application designed to provide robust app locking mechanisms using overlay screens, background services, and accessibility services. It allows users to lock selected apps with a password, ensuring that sensitive apps remain protected even when re-accessed. The app monitors active apps in real-time and displays a secure overlay to block unauthorized access.</p>


<h2>Screenshots:</h2>
<img src="https://github.com/user-attachments/assets/34c2c980-1d9d-4aa4-a4a6-d00cfe7baac0" alt="Lock Screen" width="215" height="430">
<img src="https://github.com/user-attachments/assets/c65c0c39-4197-4932-bfde-ce15ea92be4a" alt="Settings" width="215" height="430">
<img src="https://github.com/user-attachments/assets/b0ff92f3-4954-4809-8f72-cdb308fe53f2" alt="App Selection" width="215" height="430">
<hr>

<h2>Project Description</h2>
<p>AppLockPro was developed as part of a mobile security project to enhance app privacy and control. The app utilizes accessibility services to detect when a locked app is opened and overlay services to display a password prompt.</p>
<p>The app provides a seamless user experience by immediately locking apps when accessed, and maintaining lock protection even if the user exits to the home screen and returns.</p>

<hr>

<h2>Features</h2>
<ul>
  <li><strong>🔒 Secure App Lock:</strong> Locks selected apps with a 4-digit password using an overlay screen to prevent unauthorized access.</li>
  <li><strong>📊 Real-Time App Detection:</strong> Uses accessibility services to detect when a locked app is opened and display the lock screen immediately.</li>
  <li><strong>📋 Usage Stats Monitoring:</strong> Utilizes UsageStatsManager API to accurately detect active apps.</li>
  <li><strong>⚙️ Customizable Lock Screen:</strong> Allows users to personalize the lock screen with messages and error prompts.</li>
  <li><strong>🔁 Continuous Security:</strong> Ensures apps remain locked even after unlocking once, requiring the correct password every time.</li>
</ul>

<hr>

<h2>How It Works</h2>
<ol>
  <li><strong>App Selection:</strong> Users select which apps to lock from a list of installed applications.</li>
  <li><strong>Continuous Monitoring:</strong> A background service constantly checks for active apps using accessibility events and the UsageStatsManager API.</li>
  <li><strong>Overlay Lock Screen:</strong> An overlay appears immediately if a locked app is detected, requiring the correct password to proceed.</li>
  <li><strong>Lock Persistence:</strong> The lock screen appears every time a locked app is accessed, regardless of prior unlocks.</li>
  <li><strong>Permission Management:</strong> Requests accessibility and overlay permissions for seamless operation.</li>
</ol>

<hr>

<h2>Built With</h2>
<ul>
  <li><strong>Android Studio:</strong> Official IDE for Android development.</li>
  <li><strong>Java:</strong> Primary programming language used.</li>
  <li><strong>Accessibility Services:</strong> For monitoring app launches and controlling the lock screen.</li>
  <li><strong>UsageStatsManager API:</strong> For detecting active apps accurately.</li>
</ul>

<hr>

<h2>Key Implementation Details</h2>
<ul>
  <li><strong>Background Service:</strong> Runs continuously to monitor app usage using AccessibilityService and UsageStatsManager.</li>
  <li><strong>Overlay Service:</strong> Displays a password prompt on top of locked apps using the TYPE_APPLICATION_OVERLAY permission.</li>
  <li><strong>Efficient Permission Handling:</strong> Requests permissions only when required and provides user prompts for missing permissions.</li>
  <li><strong>Persistent Security:</strong> Ensures locked apps remain secure even after unlocking once, by resetting lock status when the app is re-opened.</li>
</ul>

<hr>

<h2>Permissions Required</h2>
<p>AppLockPro requires the following permissions for proper functionality:</p>
<ul>
  <li><code>android.permission.PACKAGE_USAGE_STATS</code> – To detect active apps accurately.</li>
  <li><code>android.permission.SYSTEM_ALERT_WINDOW</code> – For displaying the lock screen overlay.</li>
  <li><code>android.permission.BIND_ACCESSIBILITY_SERVICE</code> – For monitoring app access and controlling the lock screen.</li>
</ul>
<p>Ensure to grant these permissions during setup for seamless performance.</p>

<hr>

<h2>Installation and Setup</h2>
<ol>
  <li>Clone the repository:
   <pre><code>git clone https://github.com/YourUsername/AppLockPro.git</code></pre></li>
  <li>Open the project in <strong>Android Studio</strong>.</li>
  <li>Run the app on an Android device.
    <ul>
      <li>Grant necessary permissions when prompted.</li>
    </ul>
  </li>
</ol>

<hr>

<h2>Known Issues</h2>
<ul>
  <li>Occasional delay in detecting active apps due to UsageStatsManager limitations.</li>
  <li>Overlay sometimes fails to appear immediately after unlocking. This is being investigated.</li>
  <li>Accessibility Service may need to be manually enabled if disabled by the system.</li>
</ul>

<hr>

<h2>Future Enhancements</h2>
<ul>
  <li>🔒 <strong>Biometric Authentication:</strong> Integrate fingerprint or face unlock for faster access(Next Version).</li>
  <li>📊 <strong>Security Reports:</strong> Provide detailed logs of access attempts for locked apps.</li>
  <li>📱 <strong>Multi-User Support:</strong> Allow separate lock configurations for different users on shared devices.</li>
</ul>

<hr>

<h2>Contact</h2>
<p>For any inquiries or feedback, please contact <strong>your-email@example.com</strong>.</p>

<hr>

<h2>🔒 AppLockPro – Your Privacy, Our Priority! 🔒</h2>
<a href="#top">Back to Top</a>
