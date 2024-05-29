![Banner](https://divestos.org/images/featureGraphics/Extirpater.png)

Extirpater
==========

Overview
--------
A high performance free space eraser.

[<img src="https://fdroid.gitlab.io/artwork/badge/get-it-on.png"
     alt="Get it on F-Droid"
     height="80">](https://f-droid.org/packages/us.spotco.extirpater/)
[<img src="https://hosted.weblate.org/widget/divestos/extirpater/287x66-grey.png"
     alt="Translation status"
     height="66">](https://hosted.weblate.org/engage/divestos/)

What is free space erasing?
---------------------------
- Typically when you delete a file it is not really deleted, it is merely removed from the file system's index.
- A free space eraser tool such as this one fills the remaining space of your drive with random noise files and then deletes them.
- This process makes deleted files for the most part irrecoverable.

What is file table filling?
---------------------------
- On some file systems, deleted file names can still be accessible in backup index databases.
- By creating many tens or hundreds of thousands of empty files with different random names you can push out the old files.
- This process makes deleted file names for the most part irrecoverable.

Requirements
------------
- Android KitKit 4.4.4 and higher

Uses
----
- Before selling your device
- After enabling encryption
- After deleting many apps/files
- To maintain good data hygiene

Warnings
-------
- Do not overuse this tool
- Excessive use will destroy your NAND flash storage
- Ensure important files are backed up before use

Instructions
------------
0. (Optional) Delete unnecessary files, clear histories, clear app cache, factory reset, etc.
1. Launch the app
2. (Optional) Change the erase options from the menu
3. Click "Start" on either storage location
4. The status of drives are shown via the status label and progress bar

"Data Output" Option
--------------------
- The list is ranked roughly by how "secure" the output is
- If you want a super quick erase use "Zeroes", but be warned that it might not do anything due to various factors (flash, cache, compression, etc.)
- If you want a quick erase use "Random"
- If you want a quick but more secure erase use "CMWC4096RNG"
- If you want a cryptographically secure, but very slow erase use "SecureRandom"

Known Issues
------------
- On devices without real external storage, the two shown are both internal
- The file table of the secondary drive will never really be filled
- The last 20MB aren't erased

Limitations
-----------
- Due to how flash drives work and the partition layout of Android devices, it'll never be possible to fully fill the drive

Planned Updates
---------------
- Better GUI
- Add a fast csprng data source
- Root support for filling /cache and /system
- Root support for fstrim'ing partitions

Goals
-----
- Be fast
- Don't eat batteries
- Use minimal permissions
- Use libraries only when necessary

Credits
-------
- @inkhorn for Portuguese translations to the app.
- @lucasmz for Portuguese translations to the app and Fastlane metadata.
- Dimitris Vagiakakos for the Greek translations.
- @Calvineries for the French translations.
- Library: Uncommons Maths, License: Apache 2.0, https://maths.uncommons.org
- Icons: Google/Android/AOSP, License: Apache 2.0, https://google.github.io/material-design-icons/

Donate
-------
- https://divested.dev/donate
