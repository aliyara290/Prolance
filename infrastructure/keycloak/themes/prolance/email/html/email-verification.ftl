<html lang="en">
<head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge" />
    <title>Verify your email — Prolance</title>
    <style>
        * { box-sizing: border-box; margin: 0; padding: 0; }

        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'Inter', Helvetica, Arial, sans-serif;
            background-color: #f0f2f5;
            color: #121320;
            -webkit-font-smoothing: antialiased;
        }

        /* ── Outer wrapper ── */
        .email-wrapper {
            max-width: 580px;
            margin: 40px auto;
            padding: 0 16px 40px;
        }

        /* ── Logo bar ── */
        .logo-bar {
            text-align: center;
            padding: 0 0 24px;
        }
        .logo-lockup {
            display: inline-flex;
            align-items: center;
            gap: 10px;
            text-decoration: none;
        }
        .logo-icon {
            width: 40px;
            height: 40px;
            background-color: #267af7;
            border-radius: 8px;
            display: inline-flex;
            align-items: center;
            justify-content: center;
        }
        .logo-name {
            font-size: 20px;
            font-weight: 700;
            color: #121320;
            letter-spacing: -0.02em;
        }

        /* ── Card ── */
        .card {
            background-color: #ffffff;
            border: 1px solid #e2e8f0;
            border-radius: 16px;
            overflow: hidden;
            box-shadow: 0 4px 24px rgba(15, 23, 42, 0.08);
        }

        /* ── Card header accent strip ── */
        .card-accent {
            height: 5px;
            background: linear-gradient(90deg, #267af7 0%, #60a5fa 100%);
        }

        /* ── Hero section ── */
        .card-hero {
            background: linear-gradient(145deg, #eef4ff 0%, #f5f7ff 100%);
            padding: 40px 40px 32px;
            text-align: center;
            border-bottom: 1px solid #e2e8f0;
        }

        /* SVG envelope illustration */
        .hero-illustration {
            width: 120px;
            height: 120px;
            margin: 0 auto 24px;
            display: block;
        }

        .card-hero-title {
            font-size: 22px;
            font-weight: 700;
            color: #121320;
            letter-spacing: -0.02em;
            margin-bottom: 10px;
        }

        .card-hero-sub {
            font-size: 14px;
            color: #64748b;
            line-height: 1.6;
            max-width: 340px;
            margin: 0 auto;
        }
        .card-hero-sub strong {
            color: #121320;
            font-weight: 600;
        }

        /* ── Card body ── */
        .card-body {
            padding: 36px 40px 28px;
        }

        .greeting {
            font-size: 15px;
            color: #555555;
            line-height: 1.65;
            margin-bottom: 20px;
        }

        /* ── CTA button ── */
        .btn-container {
            text-align: center;
            margin: 28px 0;
        }
        .btn {
            display: inline-block;
            background-color: #267af7;
            color: #ffffff !important;
            text-decoration: none;
            font-weight: 600;
            font-size: 15px;
            padding: 14px 36px;
            border-radius: 8px;
            letter-spacing: 0.01em;
            box-shadow: 0 2px 8px rgba(38, 122, 247, 0.35);
        }
        .btn:hover { background-color: #1464dc; }

        /* ── Expiry notice ── */
        .expiry-notice {
            display: flex;
            align-items: flex-start;
            gap: 10px;
            background-color: #fef3c7;
            border: 1px solid #f59e0b;
            border-radius: 8px;
            padding: 12px 14px;
            margin: 0 0 20px;
        }
        .expiry-icon {
            width: 18px;
            height: 18px;
            flex-shrink: 0;
            margin-top: 1px;
            color: #b45309;
        }
        .expiry-text {
            font-size: 13px;
            color: #92400e;
            line-height: 1.5;
        }
        .expiry-text strong { color: #78350f; }

        /* ── Link fallback ── */
        .link-fallback {
            background-color: #f8fafc;
            border: 1px solid #e2e8f0;
            border-radius: 8px;
            padding: 12px 14px;
            margin-top: 8px;
        }
        .link-fallback-label {
            font-size: 12px;
            color: #64748b;
            margin-bottom: 6px;
        }
        .link-fallback a {
            font-size: 12px;
            color: #267af7;
            word-break: break-all;
        }

        /* ── Divider ── */
        .divider {
            border: none;
            border-top: 1px solid #e2e8f0;
            margin: 24px 0;
        }

        /* ── Security notice ── */
        .security-notice {
            font-size: 13px;
            color: #94a3b8;
            line-height: 1.6;
        }

        /* ── Footer ── */
        .email-footer {
            text-align: center;
            margin-top: 24px;
            font-size: 12px;
            color: #94a3b8;
            line-height: 1.8;
        }
        .email-footer a {
            color: #64748b;
            text-decoration: none;
        }
        .email-footer a:hover { text-decoration: underline; }

        /* ── Responsive ── */
        @media (max-width: 600px) {
            .card-hero, .card-body { padding: 28px 24px; }
            .card-hero-title { font-size: 19px; }
            .hero-illustration { width: 100px; height: 100px; }
        }
    </style>
</head>
<body>
<div class="email-wrapper">

    <!-- Logo bar -->
    <div class="logo-bar">
        <span class="logo-lockup">
            <span class="logo-icon">
                <svg width="22" height="22" fill="none" viewBox="0 0 24 24" stroke="white" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round" d="M13 10V3L4 14h7v7l9-11h-7z" />
                </svg>
            </span>
            <span class="logo-name">Prolance</span>
        </span>
    </div>

    <!-- Card -->
    <div class="card">

        <!-- Accent strip -->
        <div class="card-accent"></div>

        <!-- Hero -->
        <div class="card-hero">

            <!-- Inline SVG illustration -->
            <svg class="hero-illustration" viewBox="0 0 120 120" fill="none" xmlns="http://www.w3.org/2000/svg" aria-hidden="true">
                <!-- Background circle -->
                <circle cx="60" cy="60" r="56" fill="#eef4ff"/>

                <!-- Decorative leaves left -->
                <ellipse cx="22" cy="72" rx="10" ry="18" transform="rotate(-30 22 72)" fill="#c7d9ff" opacity="0.6"/>
                <ellipse cx="30" cy="80" rx="7" ry="14" transform="rotate(-45 30 80)" fill="#bfcfff" opacity="0.5"/>

                <!-- Decorative leaves right -->
                <ellipse cx="98" cy="72" rx="10" ry="18" transform="rotate(30 98 72)" fill="#c7d9ff" opacity="0.6"/>
                <ellipse cx="90" cy="80" rx="7" ry="14" transform="rotate(45 90 80)" fill="#bfcfff" opacity="0.5"/>

                <!-- Envelope body -->
                <rect x="22" y="44" width="68" height="44" rx="8" fill="white" stroke="#267af7" stroke-width="2.2"/>

                <!-- Envelope flap V -->
                <path d="M22 52l34 24 34-24" stroke="#267af7" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"/>

                <!-- Letter sticking out -->
                <rect x="36" y="28" width="40" height="30" rx="5" fill="white" stroke="#cbd5e1" stroke-width="1.5"/>
                <line x1="44" y1="38" x2="68" y2="38" stroke="#e2e8f0" stroke-width="2" stroke-linecap="round"/>
                <line x1="44" y1="44" x2="64" y2="44" stroke="#e2e8f0" stroke-width="2" stroke-linecap="round"/>
                <line x1="44" y1="50" x2="60" y2="50" stroke="#e2e8f0" stroke-width="2" stroke-linecap="round"/>

                <!-- Green check badge -->
                <circle cx="84" cy="38" r="14" fill="#16a34a"/>
                <path d="M78 38l5 5 9-9" stroke="white" stroke-width="2.5" stroke-linecap="round" stroke-linejoin="round"/>
            </svg>

            <h1 class="card-hero-title">Verify your email address</h1>
            <p class="card-hero-sub">
                You've entered <strong>${user.email!''}</strong> as the email address for your account.<br/>
                Please verify this email address by clicking the button below.
            </p>
        </div>

        <!-- Body -->
        <div class="card-body">

            <p class="greeting">Hello,</p>
            <p class="greeting">
                Someone registered a new account at <strong>Prolance</strong> using this email address.
                If this was you, please confirm your email address by clicking the button below.
            </p>

            <!-- CTA Button -->
            <div class="btn-container">
                <a href="${link}" class="btn">Verify Email Address</a>
            </div>

            <!-- Expiry notice -->
            <div class="expiry-notice">
                <svg class="expiry-icon" fill="none" viewBox="0 0 24 24" stroke="#b45309" stroke-width="2">
                    <path stroke-linecap="round" stroke-linejoin="round"
                          d="M12 8v4l3 3m6-3a9 9 0 11-18 0 9 9 0 0118 0z"/>
                </svg>
                <p class="expiry-text">
                    ⏱ This link will expire in
                    <strong>${linkExpirationFormatter(linkExpiration)}</strong>.
                    If you didn't create an account, you can safely ignore this email.
                </p>
            </div>

            <hr class="divider"/>

            <p class="security-notice">
                If the button doesn't work, copy and paste the link below into your browser:
            </p>

            <!-- Fallback link -->
            <div class="link-fallback">
                <p class="link-fallback-label">Verification link:</p>
                <a href="${link}">${link}</a>
            </div>

        </div>
    </div>

    <!-- Footer -->
    <div class="email-footer">
        <p>© 2026 Prolance Inc. All rights reserved.</p>
        <p>
            <a href="#">Privacy Policy</a>
            &nbsp;·&nbsp;
            <a href="#">Terms of Service</a>
        </p>
        <p style="margin-top:8px; color:#b4bfcc;">
            You received this email because you registered on Prolance.
        </p>
    </div>

</div>
</body>
</html>
