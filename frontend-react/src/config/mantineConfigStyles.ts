import { createTheme } from "@mantine/core";
import type {MantineColorsTuple} from "@mantine/core"
import colorPalette from "@/constants/colorPalette";

// Define custom color tuples for Mantine (10 shades each)
// Primary color based on your #D9D9D9 gray palette
const primaryColor: MantineColorsTuple = [
  "#FFFFFF", // lightest
  "#F5F5F5",
  "#E5E5E5",
  "#D9D9D9",
  "#C0C0C0",
  "#999999", // your accent
  "#808080",
  "#666666",
  "#4D4D4D",
  "#3C3C3C", // darkest (your tertiary bg)
];

// Dark grays for backgrounds
const darkColor: MantineColorsTuple = [
  "#3C3C3C", // lightest (your tertiary)
  "#333333",
  "#2A2A2A",
  "#202123",
  "#1A1A1A",
  "#111111", // your secondary bg
  "#0D0D0D",
  "#0A0A0A", // your main bg
  "#050505",
  "#000000", // darkest
];

export const mantineTheme = createTheme({
  // Color scheme
  primaryColor: "primary",
  primaryShade: 0, // index 0 = #FFFFFF
  // primaryShade: 3, // index 3 = #D9D9D9

  colors: {
    primary: primaryColor,
    dark: darkColor,
  },

  // Typography
  fontFamily: "Inter, sans-serif",
  fontFamilyMonospace: "Monaco, Courier, monospace",
  headings: {
    fontFamily: "Inter, sans-serif",
    fontWeight: "600",
  },

  // Spacing (based on your CSS variables)
  spacing: {
    xss: "0.5rem",  // --spacing-xss
    xs: "0.75rem",  // --spacing-xs
    sm: "1rem",     // --spacing-sm
    md: "1.5rem",   // --spacing-md
    lg: "2rem",     // --spacing-lg
    xl: "3rem",     // --spacing-vlg
  },

  // Radius
  radius: {
    xs: "0.3rem",
    sm: "0.6rem",
    md: "0.9rem",
    lg: "1.2rem",
    xl: "1.5rem",
  },

  // Default radius for components
  defaultRadius: "md",

  // Component defaults and styles
  components: {
    Button: {
      defaultProps: {
        radius: "md",
      },
      styles: () => ({
        root: {
          fontWeight: 500,
          transition: "all 0.2s ease",
          padding: "0 16px"
        },
      }),
      // eslint-disable-next-line @typescript-eslint/no-explicit-any
      vars: (_theme: any, props: any) => {
        // Ustaw CSS variables na podstawie wariantu
        if (props.variant === 'filled' || props.variant === 'primary' || !props.variant) {
          return {
            root: {
              '--button-bg': colorPalette.white,
              '--button-color': colorPalette.background,
              '--button-hover': colorPalette.primary,
              '--button-bd': 'none',
            },
          };
        }

        if (props.variant === 'outline') {
          return {
            root: {
              '--button-bg': 'transparent',
              '--button-color': colorPalette.text,
              '--button-bd': `1px solid ${colorPalette.strokePrimary}`,
              '--button-hover': colorPalette.backgroundTertiary,
              '--button-hover-bd': colorPalette.accent,
            },
          };
        }

        if (props.variant === 'subtle') {
          return {
            root: {
              '--button-bg': 'transparent',
              '--button-color': colorPalette.text,
              '--button-hover': colorPalette.backgroundSecondary,
            },
          };
        }

        if (props.variant === 'light') {
          return {
            root: {
              '--button-bg': colorPalette.backgroundSecondary,
              '--button-color': colorPalette.text,
              '--button-hover': colorPalette.backgroundTertiary,
            },
          };
        }

        return { root: {} };
      },
    },

    Input: {
      styles: () => ({
        input: {
          backgroundColor: colorPalette.backgroundSecondary,
          borderColor: colorPalette.strokePrimary,
          color: colorPalette.text,

          "&:focus": {
            borderColor: colorPalette.accent,
          },

          "&::placeholder": {
            color: colorPalette.textMuted,
          },
        },
      }),
    },

    TextInput: {
      styles: () => ({
        input: {
          backgroundColor: colorPalette.backgroundSecondary,
          borderColor: colorPalette.strokePrimary,
          color: colorPalette.text,

          "&:focus": {
            borderColor: colorPalette.accent,
          },
        },
        label: {
          color: colorPalette.text,
        },
      }),
    },

    Textarea: {
      styles: () => ({
        input: {
          backgroundColor: colorPalette.backgroundSecondary,
          borderColor: colorPalette.strokePrimary,
          color: colorPalette.text,

          "&:focus": {
            borderColor: colorPalette.accent,
          },
        },
        label: {
          color: colorPalette.text,
        },
      }),
    },

    Select: {
      styles: () => ({
        input: {
          backgroundColor: colorPalette.backgroundSecondary,
          borderColor: colorPalette.strokePrimary,
          color: colorPalette.text,

          "&:focus": {
            borderColor: colorPalette.accent,
          },
        },
        dropdown: {
          backgroundColor: colorPalette.backgroundSecondary,
          borderColor: colorPalette.strokePrimary,
        },
        option: {
          color: colorPalette.text,

          "&[data-hovered]": {
            backgroundColor: colorPalette.backgroundTertiary,
          },
        },
      }),
    },

    Modal: {
      styles: () => ({
        content: {
          backgroundColor: colorPalette.backgroundSecondary,
          color: colorPalette.text,
        },
        header: {
          backgroundColor: colorPalette.backgroundSecondary,
          color: colorPalette.textActive,
        },
        overlay: {
          backgroundColor: "rgba(0, 0, 0, 0.75)",
        },
      }),
    },

    Paper: {
      styles: () => ({
        root: {
          backgroundColor: colorPalette.backgroundSecondary,
          color: colorPalette.text,
        },
      }),
    },

    Card: {
      styles: () => ({
        root: {
          backgroundColor: colorPalette.backgroundSecondary,
          borderColor: colorPalette.strokePrimary,
          color: colorPalette.text,
        },
      }),
    },

    Popover: {
      styles: () => ({
        dropdown: {
          padding: 10,
          backgroundColor: colorPalette.backgroundSecondary,
          borderColor: colorPalette.strokePrimary,
          color: colorPalette.text,
        },
      }),
    },

    Menu: {
      styles: () => ({
        dropdown: {
          backgroundColor: colorPalette.backgroundSecondary,
          borderColor: colorPalette.strokePrimary,
        },
        item: {
          color: colorPalette.text,

          "&[data-hovered]": {
            backgroundColor: colorPalette.backgroundTertiary,
          },
        },
      }),
    },

    Tooltip: {
      styles: () => ({
        tooltip: {
          backgroundColor: colorPalette.backgroundTertiary,
          color: colorPalette.textActive,
        },
      }),
    },

    Notification: {
      styles: () => ({
        root: {
          backgroundColor: colorPalette.backgroundSecondary,
          borderColor: colorPalette.strokePrimary,
        },
        title: {
          color: colorPalette.textActive,
        },
        description: {
          color: colorPalette.text,
        },
      }),
    },
  },

  // Other settings
  black: colorPalette.black,
  white: colorPalette.white,

  other: {
    // Custom values you can access via theme.other
    colorPalette,
  },
});

export default mantineTheme;