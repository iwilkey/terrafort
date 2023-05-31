package dev.iwilkey.terrafort.physics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.DebugDrawer;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btDispatcher;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.badlogic.gdx.physics.bullet.linearmath.btIDebugDraw;
import com.badlogic.gdx.utils.Disposable;

/**
 * Represents the core physics engine for Terrafort.
 */
public final class TerrafortPhysicsCore implements Disposable {
	
	/**
	 * Flag indicating a kinematic object in the physics engine.
	 */
	public final static byte KINEMATIC_FLAG = 1 << 1;

	/**
	 * Flag indicating a static object in the physics engine.
	 */
	public final static byte STATIC_FLAG = 1 << 2;

	/**
	 * Flag indicating a dynamic object in the physics engine.
	 */
	public final static byte DYNAMIC_FLAG = 1 << 3;

	/**
	 * Maximum number of sub-steps for physics simulation.
	 */
	private final static short MAX_SUB_STEPS = 5;

	/**
	 * Fixed time step for physics simulation.
	 */
	private final static float FIXED_TIME_STEP = 1f / 60;

	/**
	 * Bullet Physics collision configuration.
	 */
	private final btCollisionConfiguration collisionConfig;

	/**
	 * Bullet Physics collision dispatcher.
	 */
	private final btDispatcher dispatcher;

	/**
	 * Bullet Physics broadphase interface.
	 */
	private final btBroadphaseInterface broadphase;

	/**
	 * Bullet Physics discrete dynamics world.
	 */
	private final btDiscreteDynamicsWorld dynamicsWorld;

	/**
	 * Bullet Physics constraint solver.
	 */
	private final btConstraintSolver constraintSolver;

	/**
	 * Collision listener for the physics engine.
	 */
	private final TerrafortPhysicsCollisionListener listener;

	/**
	 * Debug drawer for visualizing physics colliders.
	 */
	private final DebugDrawer debugRenderer;

	/**
	 * Flag indicating whether debug mode is enabled for physics rendering.
	 */
	public boolean debugMode = false;

	public TerrafortPhysicsCore() {
		Bullet.init();
		collisionConfig = new btDefaultCollisionConfiguration();
		dispatcher = new btCollisionDispatcher(collisionConfig);
		broadphase = new btDbvtBroadphase();
		constraintSolver = new btSequentialImpulseConstraintSolver();
		dynamicsWorld = new btDiscreteDynamicsWorld(dispatcher, 
				broadphase, constraintSolver, collisionConfig);
		listener = new TerrafortPhysicsCollisionListener();
		debugRenderer = new DebugDrawer();
		debugRenderer.setDebugMode(btIDebugDraw.DebugDrawModes.DBG_MAX_DEBUG_DRAW_MODE);
		dynamicsWorld.setDebugDrawer(debugRenderer);
		setWorldGravity(new Vector3(0, 0, 0));
	}
	
	/**
	 * Advances the physics simulation by one step.
	 */
	public void tick() {
		dynamicsWorld.stepSimulation((float)Gdx.graphics.getDeltaTime(), MAX_SUB_STEPS, FIXED_TIME_STEP);
	}
	
	/**
	 * Sets the gravity of the physics world.
	 * 
	 * @param grav The gravity vector.
	 */
	public void setWorldGravity(Vector3 grav) {
		dynamicsWorld.setGravity(grav);
	}
	
	/**
	 * Returns the collision listener for the physics world.
	 * 
	 * @return The collision listener.
	 */
	public TerrafortPhysicsCollisionListener getCollisionListener() {
		return listener;
	}
	
	/**
	 * Returns the dynamics world of the physics engine.
	 * 
	 * @return The dynamics world.
	 */
	public btDynamicsWorld getDynamicsWorld() {
		return dynamicsWorld;
	}
	
	/**
	 * Returns the debug drawer for visualizing physics colliders.
	 * 
	 * @return The debug drawer.
	 */
	public DebugDrawer getDebugDrawer() {
		return debugRenderer;
	}
	
	@Override
	public void dispose() {
		dynamicsWorld.dispose();
		dispatcher.dispose();
		constraintSolver.dispose();
		broadphase.dispose();
		collisionConfig.dispose();
		debugRenderer.dispose();
	}

}

