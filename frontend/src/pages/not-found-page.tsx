import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";

export function NotFoundPage() {
  return <main className="grid min-h-screen place-items-center p-6 text-center"><div><p className="number text-6xl font-semibold text-primary">404</p><h1 className="mt-4 text-2xl font-semibold">Page not found</h1><p className="mt-2 text-muted-foreground">The page you requested does not exist.</p><Button asChild className="mt-6"><Link to="/">Return to overview</Link></Button></div></main>;
}
