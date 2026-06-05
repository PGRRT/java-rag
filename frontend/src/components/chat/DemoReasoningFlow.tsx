import { useEffect, useState } from "react";
import { Collapse, Text, UnstyledButton } from "@mantine/core";
import { css, keyframes } from "@emotion/css";
import colorPalette from "@/constants/colorPalette";
import { styles } from "@/constants/styles";

interface DemoStep {
    id: string;
    text: string;
    atMs: number;
    sourceLabel?: string;
}

const demoSteps: DemoStep[] = [
    {
        id: "profile",
        text: "Analiza profilu pacjenta (wiek: 75 lat, nadciśnienie)...",
        atMs: 0,
    },
    {
        id: "interactions",
        text: "Przeszukiwanie bazy interakcji lekowych...",
        atMs: 3000,
        sourceLabel: "Charakterystyka Produktu Leczniczego",
    },
    {
        id: "metabolism",
        text: "Weryfikacja szlaku metabolicznego (CYP450) dla Paracetamolu i Warfaryny...",
        atMs: 10000,
    },
    {
        id: "synthesis",
        text: "Synteza zaleceń klinicznych i generowanie ostrzeżeń...",
        atMs: 12000,
    },
];

const totalDurationMs = 16000;

const fadeInUp = keyframes`
  from {
    opacity: 0;
    transform: translateY(6px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
`;

const spin = keyframes`
  to {
    transform: rotate(360deg);
  }
`;

const SourcePill = ({ label }: { label: string }) => (
    <span
        className={css`
      display: inline-flex;
      align-items: center;
      gap: 6px;
      padding: 2px 8px;
      border-radius: 999px;
      border: 1px solid ${colorPalette.textBrand};
      color: ${colorPalette.textBrand};
      font-size: 12px;
      background-color: ${colorPalette.backgroundSecondary};
      white-space: nowrap;
    `}
    >
        <span>[</span>
        <span>📄</span>
        <span>{label}</span>
        <span>]</span>
    </span>
);

const StepRow = ({
    text,
    isActive,
    isDone,
    sourceLabel,
}: {
    text: string;
    isActive: boolean;
    isDone: boolean;
    sourceLabel?: string;
}) => (
    <div
        className={css`
      display: flex;
      gap: 10px;
      align-items: flex-start;
      animation: ${fadeInUp} 260ms ease-out;
    `}
    >
        <div
            className={css`
        width: 16px;
        height: 16px;
        margin-top: 2px;
        display: inline-flex;
        align-items: center;
        justify-content: center;
        color: ${isActive ? colorPalette.textBrand : colorPalette.textMuted};
      `}
        >
            {isActive ? (
                <span
                    className={css`
            width: 14px;
            height: 14px;
            border: 2px solid ${colorPalette.textMuted};
            border-top-color: ${colorPalette.textBrand};
            border-radius: 50%;
            animation: ${spin} 0.8s linear infinite;
          `}
                />
            ) : (
                <svg
                    width="12"
                    height="12"
                    viewBox="0 0 24 24"
                    fill="none"
                    stroke="currentColor"
                    strokeWidth="2.4"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                >
                    <polyline points="20 6 9 17 4 12" />
                </svg>
            )}
        </div>
        <div
            className={css`
        display: flex;
        flex-direction: column;
        gap: 6px;
        color: ${isDone ? colorPalette.text : colorPalette.textMuted};
        font-size: 13px;
      `}
        >
            <span>{text}</span>
            {sourceLabel && <SourcePill label={sourceLabel} />}
        </div>
    </div>
);

const IconBadge = ({
    accentColor,
    variant,
}: {
    accentColor: string;
    variant: "check" | "alert";
}) => (
    <span
        className={css`
      width: 22px;
      height: 22px;
      border-radius: 999px;
      background-color: ${accentColor};
      display: inline-flex;
      align-items: center;
      justify-content: center;
      color: ${colorPalette.black};
      flex: 0 0 auto;
    `}
    >
        {variant === "check" ? (
            <svg
                width="12"
                height="12"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.4"
                strokeLinecap="round"
                strokeLinejoin="round"
            >
                <polyline points="20 6 9 17 4 12" />
            </svg>
        ) : (
            <svg
                width="12"
                height="12"
                viewBox="0 0 24 24"
                fill="none"
                stroke="currentColor"
                strokeWidth="2.4"
                strokeLinecap="round"
                strokeLinejoin="round"
            >
                <path d="M12 9v4" />
                <path d="M12 17h.01" />
                <circle cx="12" cy="12" r="9" />
            </svg>
        )}
    </span>
);

const AnswerCard = ({
    accentColor,
    variant,
    text,
}: {
    accentColor: string;
    variant: "check" | "alert";
    text: string;
}) => (
    <div
        className={css`
      display: flex;
      gap: 12px;
      padding: 12px;
      border-radius: ${styles.borderRadius.small};
      background-color: ${colorPalette.backgroundSecondary};
      border: 1px solid ${colorPalette.strokePrimary};
      border-left: 3px solid ${accentColor};
      animation: ${fadeInUp} 260ms ease-out;
    `}
    >
        <IconBadge accentColor={accentColor} variant={variant} />
        <Text
            size="sm"
            className={css`
        color: ${colorPalette.text};
        line-height: 1.45;
      `}
        >
            {text}
        </Text>
    </div>
);

const DemoReasoningFlow = () => {
    const [visibleCount, setVisibleCount] = useState(1);
    const [isComplete, setIsComplete] = useState(false);
    const [opened, setOpened] = useState(true);

    useEffect(() => {
        const timers: number[] = [];

        demoSteps.forEach((step, index) => {
            if (index === 0) return;
            timers.push(
                window.setTimeout(() => {
                    setVisibleCount((count) => Math.max(count, index + 1));
                }, step.atMs),
            );
        });

        timers.push(
            window.setTimeout(() => {
                setIsComplete(true);
                setOpened(false);
            }, totalDurationMs),
        );

        return () => {
            timers.forEach((timer) => window.clearTimeout(timer));
        };
    }, []);

    const activeIndex = Math.min(
        Math.max(visibleCount - 1, 0),
        demoSteps.length - 1,
    );
    const durationSeconds = Math.round(totalDurationMs / 1000);

    return (
        <div
            className={css`
        margin-top: 8px;
        width: 100%;
      `}
        >
            <UnstyledButton
                onClick={() => {
                    if (isComplete) {
                        setOpened((prev) => !prev);
                    }
                }}
                className={css`
          display: inline-flex;
          align-items: center;
          gap: 8px;
          color: ${colorPalette.textMuted};
          font-size: 14px;
          padding: 4px 0;
          transition: color 0.2s;
          &:hover {
            color: ${colorPalette.textBrand};
          }
        `}
            >
                <div
                    className={css`
            width: 16px;
            height: 16px;
            display: flex;
            align-items: center;
            justify-content: center;
            transform: ${opened ? "rotate(90deg)" : "rotate(0deg)"};
            transition: transform 0.2s;
          `}
                >
                    <svg
                        width="12"
                        height="12"
                        viewBox="0 0 24 24"
                        fill="none"
                        stroke="currentColor"
                        strokeWidth="2"
                        strokeLinecap="round"
                        strokeLinejoin="round"
                    >
                        <polyline points="9 18 15 12 9 6" />
                    </svg>
                </div>
                <Text size="sm" fw={500}>
                    {isComplete
                        ? `Myślał przez ${durationSeconds} sekund >`
                        : "Myślenie..."}
                </Text>
            </UnstyledButton>

            <Collapse in={opened}>
                <div
                    className={css`
            margin-top: 8px;
            padding: 12px;
            background-color: ${colorPalette.backgroundSecondary};
            border-left: 3px solid ${colorPalette.textBrand};
            border-radius: ${styles.borderRadius.small};
            font-size: 13px;
            color: ${colorPalette.text};
            display: flex;
            flex-direction: column;
            gap: 10px;
          `}
                >
                    {demoSteps.slice(0, visibleCount).map((step, index) => {
                        const isActive = !isComplete && index === activeIndex;
                        const isDone = isComplete || index < activeIndex;

                        return (
                            <StepRow
                                key={step.id}
                                text={step.text}
                                sourceLabel={step.sourceLabel}
                                isActive={isActive}
                                isDone={isDone}
                            />
                        );
                    })}
                </div>
            </Collapse>

            {isComplete && (
                <div
                    className={css`
            margin-top: 16px;
            display: flex;
            flex-direction: column;
            gap: 12px;
          `}
                >
                    <AnswerCard
                        accentColor="#22c55e"
                        variant="check"
                        text="Łączenie paracetamolu z warfaryną wymaga szczególnej ostrożności, zwłaszcza u osób starszych. Paracetamol może nasilać działanie przeciwzakrzepowe warfaryny, co znacząco zwiększa ryzyko krwawień. Zaleca się ścisłe monitorowanie wskaźnika INR, jeśli paracetamol jest stosowany w dawkach powyżej 2g na dobę przez kilka dni z rzędu."
                    />
                    <AnswerCard
                        accentColor="#f59e0b"
                        variant="alert"
                        text="Zawsze skonsultuj zmianę dawkowania leków przeciwzakrzepowych z lekarzem prowadzącym."
                    />
                </div>
            )}
        </div>
    );
};

export default DemoReasoningFlow;
