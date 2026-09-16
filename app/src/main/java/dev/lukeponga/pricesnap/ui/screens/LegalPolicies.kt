package dev.lukeponga.pricesnap.ui.screens

object LegalPolicies {
    const val PRIVACY_POLICY = """PriceSnap – Privacy Policy (Prototype Version)
Last updated: 16 September 2026
Owner: Luke Ponga, trading as PriceSnap
Contact: lukeponga9@gmail.com

1. Introduction
PriceSnap is an experimental Android prototype that provides AI-generated resale value estimates from photos submitted by users. This policy explains what information is processed, how it is used, and the rights available to you. Features and data flows may change as the service evolves.

2. Information We Process
PriceSnap processes photos you capture or select for appraisal. The Android app also stores appraisal results, timestamps, item details, and local thumbnail file paths in a private Room database on your device. PriceSnap does not currently provide user accounts or cloud-synchronised scan history.

The app does not operate a separate analytics or advertising system. Google, Vercel, Android, or your device may generate ordinary operational, security, or crash information under their own terms and settings.

3. How Photos Are Used
Your photo is encoded and sent over HTTPS to PriceSnap's Vercel API endpoint, which forwards it to Google's free-tier Gemini API for analysis. Under Google's applicable free-tier terms, submitted inputs may be reviewed by humans and used to improve Google products and machine-learning technologies. Do not submit sensitive, confidential, or identifying images.

PriceSnap uses photos only to return the requested appraisal and to maintain an optional local thumbnail on your device. PriceSnap does not use submitted photos for its own model training, advertising, or profiling.

4. Local Storage
Saved appraisal history and thumbnail images remain on your device until you delete scan history, clear PriceSnap's app data, or uninstall the app. PriceSnap disables Android cloud backup for its app data. Local Room data is private to the app but is not separately encrypted by PriceSnap beyond protections supplied by Android and your device.

5. Overseas Processing
Google and Vercel may process information outside New Zealand, including in the United States and other countries where they or their service providers operate. Their handling of information is governed by their applicable terms and privacy policies.

6. Retention and Deletion
PriceSnap does not intentionally create a permanent server-side image library. The Vercel endpoint processes the image to fulfil the appraisal request. Google may retain submitted content under its free-tier Gemini terms; PriceSnap cannot directly retrieve or delete copies controlled by Google.

Use Settings > Delete scan history to remove saved Room records and their associated cached image files from the device. You can also clear all PriceSnap app data through Android settings or uninstall the app.

7. Minimum Age
PriceSnap is not intended for anyone under 18 years of age. By using the prototype, you confirm that you are at least 18.

8. Sharing
PriceSnap does not sell personal information or share it with advertisers. Information is disclosed only to service providers required to operate the prototype, including Vercel for the API endpoint and Google Gemini for image analysis, or where disclosure is required by law.

9. Security
PriceSnap uses HTTPS for data in transit, avoids logging photo request bodies in the Android app, restricts history to local app storage, and disables Android cloud backup. No system is completely secure, so users should avoid submitting sensitive or confidential images.

10. Your Rights
You may ask whether PriceSnap controls personal information about you and request access, correction, or deletion by contacting the email below. PriceSnap may be unable to identify anonymous provider logs or delete copies independently controlled by Google or Vercel.

11. Changes
This policy may be updated as PriceSnap moves from prototype to production. Material changes will be communicated through the app, website, or repository where reasonably practicable.

12. Contact
Luke Ponga, trading as PriceSnap
Email: lukeponga9@gmail.com"""

    const val DATA_DELETION_POLICY = """PriceSnap – Data Deletion Policy (Prototype Version)
Last updated: 16 September 2026
Owner: Luke Ponga, trading as PriceSnap
Contact: lukeponga9@gmail.com

1. Introduction
This policy explains how information can be removed from the PriceSnap Android prototype. PriceSnap currently has no user accounts or cloud-synchronised history.

2. Delete Scan History in PriceSnap
Open Settings and select Delete scan history. After you confirm, PriceSnap permanently deletes all appraisal records from its local Room database and deletes the associated cached thumbnail files from the device. This action cannot be undone.

3. Delete All Local App Data
You can delete all remaining PriceSnap data through Android Settings > Apps > PriceSnap > Storage > Clear storage. Uninstalling PriceSnap also removes its private local app data. Android cloud backup is disabled for PriceSnap.

4. Submitted Images
PriceSnap sends submitted photos through its Vercel API endpoint to Google's free-tier Gemini API. PriceSnap does not intentionally retain a permanent server-side copy after the request is processed. Google may retain, review, or use submitted content under its free-tier terms. PriceSnap cannot retrieve or delete information independently controlled by Google.

5. Operational Records
PriceSnap does not operate a separate analytics platform in the Android prototype. Vercel, Google, Android, or your device may retain operational, security, or crash records under their own policies. PriceSnap may be unable to identify or delete anonymous or provider-controlled records.

6. Deletion Requests
For questions or requests concerning information controlled by PriceSnap, email lukeponga9@gmail.com. Include the approximate date and time of use and a description of the issue. Do not resend the original image unless requested and you choose to do so.

7. Legal and Security Retention
Limited information may be retained where reasonably necessary to investigate abuse or a security incident, comply with law, or establish or defend a legal claim. It will be removed when no longer required.

8. Minimum Age
PriceSnap is not intended for anyone under 18. If PriceSnap learns that it controls information submitted by a person under 18, it will take reasonable steps to delete that information.

9. Changes
This policy may change as PriceSnap introduces accounts, persistent cloud storage, paid AI services, or different infrastructure. The updated policy will show a revised date.

10. Contact
Luke Ponga, trading as PriceSnap
Email: lukeponga9@gmail.com"""
}
